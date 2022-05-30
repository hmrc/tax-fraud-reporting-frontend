#!/bin/bash

echo ""
echo "Applying migration BusinessChooseYourAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /businessChooseYourAddress                        controllers.BusinessChooseYourAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /businessChooseYourAddress                        controllers.BusinessChooseYourAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBusinessChooseYourAddress                  controllers.BusinessChooseYourAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBusinessChooseYourAddress                  controllers.BusinessChooseYourAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "businessChooseYourAddress.title = BusinessChooseYourAddress" >> ../conf/messages.en
echo "businessChooseYourAddress.heading = BusinessChooseYourAddress" >> ../conf/messages.en
echo "businessChooseYourAddress.yes = Yes" >> ../conf/messages.en
echo "businessChooseYourAddress.no = No" >> ../conf/messages.en
echo "businessChooseYourAddress.checkYourAnswersLabel = BusinessChooseYourAddress" >> ../conf/messages.en
echo "businessChooseYourAddress.error.required = Select businessChooseYourAddress" >> ../conf/messages.en
echo "businessChooseYourAddress.change.hidden = BusinessChooseYourAddress" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessChooseYourAddressUserAnswersEntry: Arbitrary[(BusinessChooseYourAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[BusinessChooseYourAddressPage.type]";\
    print "        value <- arbitrary[BusinessChooseYourAddress].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessChooseYourAddressPage: Arbitrary[BusinessChooseYourAddressPage.type] =";\
    print "    Arbitrary(BusinessChooseYourAddressPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessChooseYourAddress: Arbitrary[BusinessChooseYourAddress] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(BusinessChooseYourAddress.values.toSeq)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(BusinessChooseYourAddressPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration BusinessChooseYourAddress completed"
