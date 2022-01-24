#!/bin/bash

echo ""
echo "Applying migration IndividualConnection"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualConnection                        controllers.IndividualConnectionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualConnection                        controllers.IndividualConnectionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualConnection                  controllers.IndividualConnectionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualConnection                  controllers.IndividualConnectionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualConnection.title = How do you know the individual?" >> ../conf/messages.en
echo "individualConnection.heading = How do you know the individual?" >> ../conf/messages.en
echo "individualConnection.partner = Partner" >> ../conf/messages.en
echo "individualConnection.exPartner = Ex-Partner" >> ../conf/messages.en
echo "individualConnection.checkYourAnswersLabel = How do you know the individual?" >> ../conf/messages.en
echo "individualConnection.error.required = Select individualConnection" >> ../conf/messages.en
echo "individualConnection.change.hidden = IndividualConnection" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualConnectionUserAnswersEntry: Arbitrary[(IndividualConnectionPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualConnectionPage.type]";\
    print "        value <- arbitrary[IndividualConnection].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualConnectionPage: Arbitrary[IndividualConnectionPage.type] =";\
    print "    Arbitrary(IndividualConnectionPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualConnection: Arbitrary[IndividualConnection] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(IndividualConnection.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualConnectionPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration IndividualConnection completed"
