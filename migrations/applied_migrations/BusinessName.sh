#!/bin/bash

echo ""
echo "Applying migration BusinessName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /businessName                        controllers.BusinessNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /businessName                        controllers.BusinessNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBusinessName                  controllers.BusinessNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBusinessName                  controllers.BusinessNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "businessName.title = businessName" >> ../conf/messages.en
echo "businessName.heading = businessName" >> ../conf/messages.en
echo "businessName.checkYourAnswersLabel = businessName" >> ../conf/messages.en
echo "businessName.error.required = Enter businessName" >> ../conf/messages.en
echo "businessName.error.length = BusinessName must be 250 characters or less" >> ../conf/messages.en
echo "businessName.change.hidden = BusinessName" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessNameUserAnswersEntry: Arbitrary[(BusinessNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[BusinessNamePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryBusinessNamePage: Arbitrary[BusinessNamePage.type] =";\
    print "    Arbitrary(BusinessNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(BusinessNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration BusinessName completed"
