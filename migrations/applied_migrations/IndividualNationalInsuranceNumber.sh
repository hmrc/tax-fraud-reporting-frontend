#!/bin/bash

echo ""
echo "Applying migration IndividualNationalInsuranceNumber"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualNationalInsuranceNumber                        controllers.IndividualNationalInsuranceNumberController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualNationalInsuranceNumber                        controllers.IndividualNationalInsuranceNumberController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualNationalInsuranceNumber                  controllers.IndividualNationalInsuranceNumberController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualNationalInsuranceNumber                  controllers.IndividualNationalInsuranceNumberController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualNationalInsuranceNumber.title = individualNationalInsuranceNumber" >> ../conf/messages.en
echo "individualNationalInsuranceNumber.heading = individualNationalInsuranceNumber" >> ../conf/messages.en
echo "individualNationalInsuranceNumber.checkYourAnswersLabel = individualNationalInsuranceNumber" >> ../conf/messages.en
echo "individualNationalInsuranceNumber.error.required = Enter individualNationalInsuranceNumber" >> ../conf/messages.en
echo "individualNationalInsuranceNumber.error.length = IndividualNationalInsuranceNumber must be 13 characters or less" >> ../conf/messages.en
echo "individualNationalInsuranceNumber.change.hidden = IndividualNationalInsuranceNumber" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualNationalInsuranceNumberUserAnswersEntry: Arbitrary[(IndividualNationalInsuranceNumberPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualNationalInsuranceNumberPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualNationalInsuranceNumberPage: Arbitrary[IndividualNationalInsuranceNumberPage.type] =";\
    print "    Arbitrary(IndividualNationalInsuranceNumberPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualNationalInsuranceNumberPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration IndividualNationalInsuranceNumber completed"
