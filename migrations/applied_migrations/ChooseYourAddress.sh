#!/bin/bash

echo ""
echo "Applying migration ChooseYourAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /chooseYourAddress                        controllers.ChooseYourAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /chooseYourAddress                        controllers.ChooseYourAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeChooseYourAddress                  controllers.ChooseYourAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeChooseYourAddress                  controllers.ChooseYourAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "chooseYourAddress.title = Choose your address" >> ../conf/messages.en
echo "chooseYourAddress.heading = Choose your address" >> ../conf/messages.en
echo "chooseYourAddress.addressAdA[3~[3~Address = Address" >> ../conf/messages.en
echo "chooseYourAddress.ds = dd" >> ../conf/messages.en
echo "chooseYourAddress.checkYourAnswersLabel = Choose your address" >> ../conf/messages.en
echo "chooseYourAddress.error.required = Select chooseYourAddress" >> ../conf/messages.en
echo "chooseYourAddress.change.hidden = ChooseYourAddress" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryChooseYourAddressUserAnswersEntry: Arbitrary[(ChooseYourAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ChooseYourAddressPage.type]";\
    print "        value <- arbitrary[ChooseYourAddress].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryChooseYourAddressPage: Arbitrary[ChooseYourAddressPage.type] =";\
    print "    Arbitrary(ChooseYourAddressPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryChooseYourAddress: Arbitrary[ChooseYourAddress] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(ChooseYourAddress.values.toSeq)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ChooseYourAddressPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration ChooseYourAddress completed"
