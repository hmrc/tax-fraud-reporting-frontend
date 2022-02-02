#!/bin/bash

echo ""
echo "Applying migration BusinessContactDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /businessContactDetails                        controllers.BusinessContactDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /businessContactDetails                        controllers.BusinessContactDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBusinessContactDetails                  controllers.BusinessContactDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBusinessContactDetails                  controllers.BusinessContactDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "businessContactDetails.title = businessContactDetails" >> ../conf/messages.en
echo "businessContactDetails.heading = businessContactDetails" >> ../conf/messages.en
echo "businessContactDetails.checkYourAnswersLabel = businessContactDetails" >> ../conf/messages.en
echo "businessContactDetails.error.required = Enter businessContactDetails" >> ../conf/messages.en
echo "businessContactDetails.error.length = BusinessContactDetails must be 1001100 characters or less" >> ../conf/messages.en
echo "businessContactDetails.change.hidden = BusinessContactDetails" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessContactDetailsUserAnswersEntry: Arbitrary[(BusinessContactDetailsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[BusinessContactDetailsPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessContactDetailsPage: Arbitrary[BusinessContactDetailsPage.type] =";\
    print "    Arbitrary(BusinessContactDetailsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(BusinessContactDetailsPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration BusinessContactDetails completed"
