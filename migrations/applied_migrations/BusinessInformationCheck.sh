#!/bin/bash

echo ""
echo "Applying migration BusinessInformationCheck"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /businessInformationCheck                        controllers.BusinessInformationCheckController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /businessInformationCheck                        controllers.BusinessInformationCheckController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBusinessInformationCheck                  controllers.BusinessInformationCheckController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBusinessInformationCheck                  controllers.BusinessInformationCheckController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "businessInformationCheck.title = What do you know about the busiess?" >> ../conf/messages.en
echo "businessInformationCheck.heading = What do you know about the busiess?" >> ../conf/messages.en
echo "businessInformationCheck.name = name" >> ../conf/messages.en
echo "businessInformationCheck.typety[typetypetyeptype = type" >> ../conf/messages.en
echo "businessInformationCheck.checkYourAnswersLabel = What do you know about the busiess?" >> ../conf/messages.en
echo "businessInformationCheck.error.required = Select businessInformationCheck" >> ../conf/messages.en
echo "businessInformationCheck.change.hidden = BusinessInformationCheck" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessInformationCheckUserAnswersEntry: Arbitrary[(BusinessInformationCheckPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[BusinessInformationCheckPage.type]";\
    print "        value <- arbitrary[BusinessInformationCheck].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessInformationCheckPage: Arbitrary[BusinessInformationCheckPage.type] =";\
    print "    Arbitrary(BusinessInformationCheckPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessInformationCheck: Arbitrary[BusinessInformationCheck] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(BusinessInformationCheck.values)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(BusinessInformationCheckPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration BusinessInformationCheck completed"
