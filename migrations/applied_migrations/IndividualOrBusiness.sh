#!/bin/bash

echo ""
echo "Applying migration IndividualOrBusiness"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualOrBusiness                        controllers.IndividualOrBusinessController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualOrBusiness                        controllers.IndividualOrBusinessController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualOrBusiness                  controllers.IndividualOrBusinessController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualOrBusiness                  controllers.IndividualOrBusinessController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualOrBusiness.title = Are you reporting an individual or a business?" >> ../conf/messages.en
echo "individualOrBusiness.heading = Are you reporting an individual or a business?" >> ../conf/messages.en
echo "individualOrBusiness.individual = individual" >> ../conf/messages.en
echo "individualOrBusiness.business = business" >> ../conf/messages.en
echo "individualOrBusiness.checkYourAnswersLabel = Are you reporting an individual or a business?" >> ../conf/messages.en
echo "individualOrBusiness.error.required = Select individualOrBusiness" >> ../conf/messages.en
echo "individualOrBusiness.change.hidden = IndividualOrBusiness" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualOrBusinessUserAnswersEntry: Arbitrary[(IndividualOrBusinessPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualOrBusinessPage.type]";\
    print "        value <- arbitrary[IndividualOrBusiness].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualOrBusinessPage: Arbitrary[IndividualOrBusinessPage.type] =";\
    print "    Arbitrary(IndividualOrBusinessPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualOrBusiness: Arbitrary[IndividualOrBusiness] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(IndividualOrBusiness.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualOrBusinessPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration IndividualOrBusiness completed"
