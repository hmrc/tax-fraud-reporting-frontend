#!/bin/bash

echo ""
echo "Applying migration ProvideContactDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /provideContactDetails                        controllers.ProvideContactDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /provideContactDetails                        controllers.ProvideContactDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeProvideContactDetails                  controllers.ProvideContactDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeProvideContactDetails                  controllers.ProvideContactDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "provideContactDetails.title = Do you wish to provide your contact details?" >> ../conf/messages.en
echo "provideContactDetails.heading = Do you wish to provide your contact details?" >> ../conf/messages.en
echo "provideContactDetails.yes = Yes" >> ../conf/messages.en
echo "provideContactDetails.no = No" >> ../conf/messages.en
echo "provideContactDetails.checkYourAnswersLabel = Do you wish to provide your contact details?" >> ../conf/messages.en
echo "provideContactDetails.error.required = Select provideContactDetails" >> ../conf/messages.en
echo "provideContactDetails.change.hidden = ProvideContactDetails" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryProvideContactDetailsUserAnswersEntry: Arbitrary[(ProvideContactDetailsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ProvideContactDetailsPage.type]";\
    print "        value <- arbitrary[ProvideContactDetails].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryProvideContactDetailsPage: Arbitrary[ProvideContactDetailsPage.type] =";\
    print "    Arbitrary(ProvideContactDetailsPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryProvideContactDetails: Arbitrary[ProvideContactDetails] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(ProvideContactDetails.values.toSeq)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ProvideContactDetailsPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration ProvideContactDetails completed"
