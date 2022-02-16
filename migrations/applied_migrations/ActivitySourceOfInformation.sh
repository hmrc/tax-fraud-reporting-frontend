#!/bin/bash

echo ""
echo "Applying migration ActivitySourceOfInformation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /activitySourceOfInformation                        controllers.ActivitySourceOfInformationController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /activitySourceOfInformation                        controllers.ActivitySourceOfInformationController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeActivitySourceOfInformation                  controllers.ActivitySourceOfInformationController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeActivitySourceOfInformation                  controllers.ActivitySourceOfInformationController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "activitySourceOfInformation.title = How do you know this information?" >> ../conf/messages.en
echo "activitySourceOfInformation.heading = How do you know this information?" >> ../conf/messages.en
echo "activitySourceOfInformation.reportedIndividuals = ReportedIndividuals" >> ../conf/messages.en
echo "activitySourceOfInformation.informationInLocalArea = InformationInLocalArea" >> ../conf/messages.en
echo "activitySourceOfInformation.checkYourAnswersLabel = How do you know this information?" >> ../conf/messages.en
echo "activitySourceOfInformation.error.required = Select activitySourceOfInformation" >> ../conf/messages.en
echo "activitySourceOfInformation.change.hidden = ActivitySourceOfInformation" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryActivitySourceOfInformationUserAnswersEntry: Arbitrary[(ActivitySourceOfInformationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ActivitySourceOfInformationPage.type]";\
    print "        value <- arbitrary[ActivitySourceOfInformation].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryActivitySourceOfInformationPage: Arbitrary[ActivitySourceOfInformationPage.type] =";\
    print "    Arbitrary(ActivitySourceOfInformationPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryActivitySourceOfInformation: Arbitrary[ActivitySourceOfInformation] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(ActivitySourceOfInformation.values.toSeq)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ActivitySourceOfInformationPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration ActivitySourceOfInformation completed"
