#!/bin/bash

echo ""
echo "Applying migration IndividualContactDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualContactDetails                        controllers.IndividualContactDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualContactDetails                        controllers.IndividualContactDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualContactDetails                  controllers.IndividualContactDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualContactDetails                  controllers.IndividualContactDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualContactDetails.title = individualContactDetails" >> ../conf/messages.en
echo "individualContactDetails.heading = individualContactDetails" >> ../conf/messages.en
echo "individualContactDetails.landlineNumber = landlineNumber" >> ../conf/messages.en
echo "individualContactDetails.mobileNumber = mobileNumber" >> ../conf/messages.en
echo "individualContactDetails.checkYourAnswersLabel = IndividualContactDetails" >> ../conf/messages.en
echo "individualContactDetails.error.landlineNumber.required = Enter landlineNumber" >> ../conf/messages.en
echo "individualContactDetails.error.mobileNumber.required = Enter mobileNumber" >> ../conf/messages.en
echo "individualContactDetails.error.landlineNumber.length = landlineNumber must be 100 characters or less" >> ../conf/messages.en
echo "individualContactDetails.error.mobileNumber.length = mobileNumber must be 100 characters or less" >> ../conf/messages.en
echo "individualContactDetails.landlineNumber.change.hidden = landlineNumber" >> ../conf/messages.en
echo "individualContactDetails.mobileNumber.change.hidden = mobileNumber" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualContactDetailsUserAnswersEntry: Arbitrary[(IndividualContactDetailsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualContactDetailsPage.type]";\
    print "        value <- arbitrary[IndividualContactDetails].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualContactDetailsPage: Arbitrary[IndividualContactDetailsPage.type] =";\
    print "    Arbitrary(IndividualContactDetailsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualContactDetails: Arbitrary[IndividualContactDetails] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        landlineNumber <- arbitrary[String]";\
    print "        mobileNumber <- arbitrary[String]";\
    print "      } yield IndividualContactDetails(landlineNumber, mobileNumber)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualContactDetailsPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration IndividualContactDetails completed"
