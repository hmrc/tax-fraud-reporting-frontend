#!/bin/bash

echo ""
echo "Applying migration SelectConnectionBusiness"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /selectConnectionBusiness                        controllers.SelectConnectionBusinessController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /selectConnectionBusiness                        controllers.SelectConnectionBusinessController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSelectConnectionBusiness                  controllers.SelectConnectionBusinessController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSelectConnectionBusiness                  controllers.SelectConnectionBusinessController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "selectConnectionBusiness.title = How do you know the business?7H" >> ../conf/messages.en
echo "selectConnectionBusiness.heading = How do you know the business?7H" >> ../conf/messages.en
echo "selectConnectionBusiness.current-employer = current-employer" >> ../conf/messages.en
echo "selectConnectionBusiness.ex-employer = ex-employer" >> ../conf/messages.en
echo "selectConnectionBusiness.checkYourAnswersLabel = How do you know the business?7H" >> ../conf/messages.en
echo "selectConnectionBusiness.error.required = Select selectConnectionBusiness" >> ../conf/messages.en
echo "selectConnectionBusiness.change.hidden = SelectConnectionBusiness" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySelectConnectionBusinessUserAnswersEntry: Arbitrary[(SelectConnectionBusinessPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SelectConnectionBusinessPage.type]";\
    print "        value <- arbitrary[SelectConnectionBusiness].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySelectConnectionBusinessPage: Arbitrary[SelectConnectionBusinessPage.type] =";\
    print "    Arbitrary(SelectConnectionBusinessPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySelectConnectionBusiness: Arbitrary[SelectConnectionBusiness] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(SelectConnectionBusiness.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SelectConnectionBusinessPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration SelectConnectionBusiness completed"
