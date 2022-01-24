#!/bin/bash

echo ""
echo "Applying migration IndividualInformation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualInformation                        controllers.IndividualInformationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualInformation                        controllers.IndividualInformationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualInformation                  controllers.IndividualInformationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualInformation                  controllers.IndividualInformationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualInformation.title = What do you know about the individual?" >> ../conf/messages.en
echo "individualInformation.heading = What do you know about the individual?" >> ../conf/messages.en
echo "individualInformation.name = Name" >> ../conf/messages.en
echo "individualInformation.age = Age (approximate)" >> ../conf/messages.en
echo "individualInformation.checkYourAnswersLabel = What do you know about the individual?" >> ../conf/messages.en
echo "individualInformation.error.required = Select individualInformation" >> ../conf/messages.en
echo "individualInformation.change.hidden = IndividualInformation" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualInformationUserAnswersEntry: Arbitrary[(IndividualInformationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualInformationPage.type]";\
    print "        value <- arbitrary[IndividualInformation].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualInformationPage: Arbitrary[IndividualInformationPage.type] =";\
    print "    Arbitrary(IndividualInformationPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualInformation: Arbitrary[IndividualInformation] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(IndividualInformation.values)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualInformationPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration IndividualInformation completed"
