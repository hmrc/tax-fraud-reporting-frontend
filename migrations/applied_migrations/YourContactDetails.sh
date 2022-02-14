#!/bin/bash

echo ""
echo "Applying migration YourContactDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourContactDetails                        controllers.YourContactDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourContactDetails                        controllers.YourContactDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourContactDetails                  controllers.YourContactDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourContactDetails                  controllers.YourContactDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourContactDetails.title = yourContactDetails" >> ../conf/messages.en
echo "yourContactDetails.heading = yourContactDetails" >> ../conf/messages.en
echo "yourContactDetails.FirstName = FirstName" >> ../conf/messages.en
echo "yourContactDetails.LastName = LastName" >> ../conf/messages.en
echo "yourContactDetails.checkYourAnswersLabel = YourContactDetails" >> ../conf/messages.en
echo "yourContactDetails.error.FirstName.required = Enter FirstName" >> ../conf/messages.en
echo "yourContactDetails.error.LastName.required = Enter LastName" >> ../conf/messages.en
echo "yourContactDetails.error.FirstName.length = FirstName must be 2552255 characters or less" >> ../conf/messages.en
echo "yourContactDetails.error.LastName.length = LastName must be 100 characters or less" >> ../conf/messages.en
echo "yourContactDetails.FirstName.change.hidden = FirstName" >> ../conf/messages.en
echo "yourContactDetails.LastName.change.hidden = LastName" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryYourContactDetailsUserAnswersEntry: Arbitrary[(YourContactDetailsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[YourContactDetailsPage.type]";\
    print "        value <- arbitrary[YourContactDetails].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryYourContactDetailsPage: Arbitrary[YourContactDetailsPage.type] =";\
    print "    Arbitrary(YourContactDetailsPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryYourContactDetails: Arbitrary[YourContactDetails] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        FirstName <- arbitrary[String]";\
    print "        LastName <- arbitrary[String]";\
    print "      } yield YourContactDetails(FirstName, LastName)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(YourContactDetailsPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration YourContactDetails completed"
