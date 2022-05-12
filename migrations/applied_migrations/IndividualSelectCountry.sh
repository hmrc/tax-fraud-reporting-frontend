#!/bin/bash

echo ""
echo "Applying migration IndividualSelectCountry"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualSelectCountry                        controllers.IndividualSelectCountryController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualSelectCountry                        controllers.IndividualSelectCountryController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualSelectCountry                  controllers.IndividualSelectCountryController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualSelectCountry                  controllers.IndividualSelectCountryController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualSelectCountry.title = individualSelectCountry" >> ../conf/messages.en
echo "individualSelectCountry.heading = individualSelectCountry" >> ../conf/messages.en
echo "individualSelectCountry.checkYourAnswersLabel = individualSelectCountry" >> ../conf/messages.en
echo "individualSelectCountry.error.required = Enter individualSelectCountry" >> ../conf/messages.en
echo "individualSelectCountry.error.length = IndividualSelectCountry must be 10011001 characters or less" >> ../conf/messages.en
echo "individualSelectCountry.change.hidden = IndividualSelectCountry" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualSelectCountryUserAnswersEntry: Arbitrary[(IndividualSelectCountryPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualSelectCountryPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualSelectCountryPage: Arbitrary[IndividualSelectCountryPage.type] =";\
    print "    Arbitrary(IndividualSelectCountryPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualSelectCountryPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration IndividualSelectCountry completed"
