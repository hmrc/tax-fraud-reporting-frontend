#!/bin/bash

echo ""
echo "Applying migration ZeroValidation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /zeroValidation                        controllers.ZeroValidationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /zeroValidation                        controllers.ZeroValidationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeZeroValidation                  controllers.ZeroValidationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeZeroValidation                  controllers.ZeroValidationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "zeroValidation.title = zeroValidation" >> ../conf/messages.en
echo "zeroValidation.heading = zeroValidation" >> ../conf/messages.en
echo "zeroValidation.checkYourAnswersLabel = zeroValidation" >> ../conf/messages.en
echo "zeroValidation.error.required = Select yes if zeroValidation" >> ../conf/messages.en
echo "zeroValidation.change.hidden = ZeroValidation" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryZeroValidationUserAnswersEntry: Arbitrary[(ZeroValidationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ZeroValidationPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryZeroValidationPage: Arbitrary[ZeroValidationPage.type] =";\
    print "    Arbitrary(ZeroValidationPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ZeroValidationPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration ZeroValidation completed"
