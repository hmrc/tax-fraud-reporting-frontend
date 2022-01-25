#!/bin/bash

echo ""
echo "Applying migration IndividualAge"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualAge                  controllers.IndividualAgeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualAge                  controllers.IndividualAgeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualAge                        controllers.IndividualAgeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualAge                        controllers.IndividualAgeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualAge.title = IndividualAge" >> ../conf/messages.en
echo "individualAge.heading = IndividualAge" >> ../conf/messages.en
echo "individualAge.checkYourAnswersLabel = IndividualAge" >> ../conf/messages.en
echo "individualAge.error.nonNumeric = Enter your individualAge using numbers" >> ../conf/messages.en
echo "individualAge.error.required = Enter your individualAge" >> ../conf/messages.en
echo "individualAge.error.wholeNumber = Enter your individualAge using whole numbers" >> ../conf/messages.en
echo "individualAge.error.outOfRange = IndividualAge must be between {0} and {1}" >> ../conf/messages.en
echo "individualAge.change.hidden = IndividualAge" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualAgeUserAnswersEntry: Arbitrary[(IndividualAgePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualAgePage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualAgePage: Arbitrary[IndividualAgePage.type] =";\
    print "    Arbitrary(IndividualAgePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualAgePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration IndividualAge completed"
