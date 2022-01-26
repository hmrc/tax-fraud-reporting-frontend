#!/bin/bash

echo ""
echo "Applying migration DateFormat"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /dateFormat                        controllers.DateFormatController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /dateFormat                        controllers.DateFormatController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDateFormat                  controllers.DateFormatController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDateFormat                  controllers.DateFormatController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "dateFormat.title = Select a format to enter this individual's age" >> ../conf/messages.en
echo "dateFormat.heading = Select a format to enter this individual's age" >> ../conf/messages.en
echo "dateFormat.date = Date of birth" >> ../conf/messages.en
echo "dateFormat.age = Approximate age" >> ../conf/messages.en
echo "dateFormat.checkYourAnswersLabel = Select a format to enter this individual's age" >> ../conf/messages.en
echo "dateFormat.error.required = Select dateFormat" >> ../conf/messages.en
echo "dateFormat.change.hidden = DateFormat" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDateFormatUserAnswersEntry: Arbitrary[(DateFormatPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DateFormatPage.type]";\
    print "        value <- arbitrary[DateFormat].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDateFormatPage: Arbitrary[DateFormatPage.type] =";\
    print "    Arbitrary(DateFormatPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDateFormat: Arbitrary[DateFormat] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(DateFormat.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DateFormatPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration DateFormat completed"
