#!/bin/bash

echo ""
echo "Applying migration BusinessConfirmAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /businessConfirmAddress                        controllers.BusinessConfirmAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /businessConfirmAddress                        controllers.BusinessConfirmAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBusinessConfirmAddress                  controllers.BusinessConfirmAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBusinessConfirmAddress                  controllers.BusinessConfirmAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "businessConfirmAddress.title = businessConfirmAddress" >> ../conf/messages.en
echo "businessConfirmAddress.heading = businessConfirmAddress" >> ../conf/messages.en
echo "businessConfirmAddress.checkYourAnswersLabel = businessConfirmAddress" >> ../conf/messages.en
echo "businessConfirmAddress.error.required = Select yes if businessConfirmAddress" >> ../conf/messages.en
echo "businessConfirmAddress.change.hidden = BusinessConfirmAddress" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessConfirmAddressUserAnswersEntry: Arbitrary[(BusinessConfirmAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[BusinessConfirmAddressPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessConfirmAddressPage: Arbitrary[BusinessConfirmAddressPage.type] =";\
    print "    Arbitrary(BusinessConfirmAddressPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(BusinessConfirmAddressPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration BusinessConfirmAddress completed"
