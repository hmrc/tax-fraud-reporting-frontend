#!/bin/bash

echo ""
echo "Applying migration FindAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /findAddress                        controllers.FindAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /findAddress                        controllers.FindAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeFindAddress                  controllers.FindAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeFindAddress                  controllers.FindAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "findAddress.title = findAddress" >> ../conf/messages.en
echo "findAddress.heading = findAddress" >> ../conf/messages.en
echo "findAddress.Postcode = Postcode" >> ../conf/messages.en
echo "findAddress.Property = Property" >> ../conf/messages.en
echo "findAddress.checkYourAnswersLabel = FindAddress" >> ../conf/messages.en
echo "findAddress.error.Postcode.required = Enter Postcode" >> ../conf/messages.en
echo "findAddress.error.Property.required = Enter Property" >> ../conf/messages.en
echo "findAddress.error.Postcode.length = Postcode must be 100 characters or less" >> ../conf/messages.en
echo "findAddress.error.Property.length = Property must be 100 characters or less" >> ../conf/messages.en
echo "findAddress.Postcode.change.hidden = Postcode" >> ../conf/messages.en
echo "findAddress.Property.change.hidden = Property" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryFindAddressUserAnswersEntry: Arbitrary[(FindAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[FindAddressPage.type]";\
    print "        value <- arbitrary[FindAddress].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryFindAddressPage: Arbitrary[FindAddressPage.type] =";\
    print "    Arbitrary(FindAddressPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryFindAddress: Arbitrary[FindAddress] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        Postcode <- arbitrary[String]";\
    print "        Property <- arbitrary[String]";\
    print "      } yield FindAddress(Postcode, Property)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(FindAddressPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration FindAddress completed"
