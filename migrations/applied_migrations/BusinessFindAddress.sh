#!/bin/bash

echo ""
echo "Applying migration BusinessFindAddress"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /businessFindAddress                        controllers.BusinessFindAddressController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /businessFindAddress                        controllers.BusinessFindAddressController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBusinessFindAddress                  controllers.BusinessFindAddressController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBusinessFindAddress                  controllers.BusinessFindAddressController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "businessFindAddress.title = businessFindAddress" >> ../conf/messages.en
echo "businessFindAddress.heading = businessFindAddress" >> ../conf/messages.en
echo "businessFindAddress.checkYourAnswersLabel = businessFindAddress" >> ../conf/messages.en
echo "businessFindAddress.error.required = Enter businessFindAddress" >> ../conf/messages.en
echo "businessFindAddress.error.length = BusinessFindAddress must be 100 characters or less" >> ../conf/messages.en
echo "businessFindAddress.change.hidden = BusinessFindAddress" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessFindAddressUserAnswersEntry: Arbitrary[(BusinessFindAddressPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[BusinessFindAddressPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessFindAddressPage: Arbitrary[BusinessFindAddressPage.type] =";\
    print "    Arbitrary(BusinessFindAddressPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(BusinessFindAddressPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration BusinessFindAddress completed"
