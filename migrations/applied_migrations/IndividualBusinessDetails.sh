#!/bin/bash

echo ""
echo "Applying migration IndividualBusinessDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualBusinessDetails                        controllers.IndividualBusinessDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualBusinessDetails                        controllers.IndividualBusinessDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualBusinessDetails                  controllers.IndividualBusinessDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualBusinessDetails                  controllers.IndividualBusinessDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualBusinessDetails.title = Does the individual have business details that you can provide?" >> ../conf/messages.en
echo "individualBusinessDetails.heading = Does the individual have business details that you can provide?" >> ../conf/messages.en
echo "individualBusinessDetails.yes = Yes" >> ../conf/messages.en
echo "individualBusinessDetails.no = No" >> ../conf/messages.en
echo "individualBusinessDetails.checkYourAnswersLabel = Does the individual have business details that you can provide?" >> ../conf/messages.en
echo "individualBusinessDetails.error.required = Select individualBusinessDetails" >> ../conf/messages.en
echo "individualBusinessDetails.change.hidden = IndividualBusinessDetails" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBusinessDetailsUserAnswersEntry: Arbitrary[(IndividualBusinessDetailsPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualBusinessDetailsPage.type]";\
    print "        value <- arbitrary[IndividualBusinessDetails].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBusinessDetailsPage: Arbitrary[IndividualBusinessDetailsPage.type] =";\
    print "    Arbitrary(IndividualBusinessDetailsPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualBusinessDetails: Arbitrary[IndividualBusinessDetails] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(IndividualBusinessDetails.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualBusinessDetailsPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration IndividualBusinessDetails completed"
