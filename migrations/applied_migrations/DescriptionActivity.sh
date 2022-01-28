#!/bin/bash

echo ""
echo "Applying migration DescriptionActivity"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /descriptionActivity                        controllers.DescriptionActivityController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /descriptionActivity                        controllers.DescriptionActivityController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDescriptionActivity                  controllers.DescriptionActivityController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDescriptionActivity                  controllers.DescriptionActivityController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "descriptionActivity.title = descriptionActivity" >> ../conf/messages.en
echo "descriptionActivity.heading = descriptionActivity" >> ../conf/messages.en
echo "descriptionActivity.checkYourAnswersLabel = descriptionActivity" >> ../conf/messages.en
echo "descriptionActivity.error.required = Enter descriptionActivity" >> ../conf/messages.en
echo "descriptionActivity.error.length = DescriptionActivity must be 250 characters or less" >> ../conf/messages.en
echo "descriptionActivity.change.hidden = DescriptionActivity" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDescriptionActivityUserAnswersEntry: Arbitrary[(DescriptionActivityPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DescriptionActivityPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDescriptionActivityPage: Arbitrary[DescriptionActivityPage.type] =";\
    print "    Arbitrary(DescriptionActivityPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DescriptionActivityPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration DescriptionActivity completed"
