#!/bin/bash

echo ""
echo "Applying migration ApproximateValue"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /approximateValue                  controllers.ApproximateValueController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /approximateValue                  controllers.ApproximateValueController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeApproximateValue                        controllers.ApproximateValueController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeApproximateValue                        controllers.ApproximateValueController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "approximateValue.title = ApproximateValue" >> ../conf/messages.en
echo "approximateValue.heading = ApproximateValue" >> ../conf/messages.en
echo "approximateValue.checkYourAnswersLabel = ApproximateValue" >> ../conf/messages.en
echo "approximateValue.error.nonNumeric = Enter your approximateValue using numbers" >> ../conf/messages.en
echo "approximateValue.error.required = Enter your approximateValue" >> ../conf/messages.en
echo "approximateValue.error.wholeNumber = Enter your approximateValue using whole numbers" >> ../conf/messages.en
echo "approximateValue.error.outOfRange = ApproximateValue must be between {0} and {1}" >> ../conf/messages.en
echo "approximateValue.change.hidden = ApproximateValue" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryApproximateValueUserAnswersEntry: Arbitrary[(ApproximateValuePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ApproximateValuePage.type]";\
    print "        value <- arbitrary[Int].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryApproximateValuePage: Arbitrary[ApproximateValuePage.type] =";\
    print "    Arbitrary(ApproximateValuePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ApproximateValuePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration ApproximateValue completed"
