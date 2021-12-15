/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.taxfraudreportingfrontend.services

import uk.gov.hmrc.taxfraudreportingfrontend.models.ActivityType

import javax.inject.Singleton

@Singleton
class ActivityTypeService {

  def getActivityTypeByCode(code: String): Option[ActivityType] = activities.find(c => c.code == code)

  def isValidActivityTypeCode(code: String): Boolean = getActivityTypeByCode(code).isDefined

  def getActivityNameByCode(code: String): Option[String] = for (c <- activities.find(c => c.code == code))
    yield c.activityName

  def getActivityByName(name: String): ActivityType = activities.find(c => c.activityName == name).get

  def getCodeByActivityName(activityName: String): Option[String] = for (
    c <- activities.find(c => c.activityName == activityName)
  ) yield c.code

  val activities = Seq(
    ActivityType(
      "22030000",
      "activityType.name.furlough",
      List("CJRS", "Furlough", "COVID", "Corona", "Coronavirus Job Retention Scheme")
    ),
    ActivityType("22030001", "activityType.name.defrauding-self-employment", List("SEISS", "COVID", "Grant", "Corona")),
    ActivityType("22030002", "activityType.name.defrauding-sick-pay", List("Illness", "Disability")),
    ActivityType("22030003", "activityType.name.defrauding-eat-out", List("EOTHO", "Restaurant")),
    ActivityType(
      "22030004",
      "activityType.name.incorrect-emp-status",
      List("Lying about status", "Not informing about change of status", "Employ", "Payroll")
    ),
    ActivityType(
      "22030005",
      "activityType.name.avoiding-tax",
      List("Avoidance", "Umbrella companies", "Offshore", "Evasion", "Tax Haven")
    ),
    ActivityType(
      "22030006",
      "activityType.name.fraud-related-tax",
      List("Value Added Tax", "Company", "Invoice", "Bill", "Receipt", "Account")
    ),
    ActivityType(
      "22030007",
      "activityType.name.defrauding-c-i-scheme",
      List("CIS", "Contractor", "Building", "Foreman", "Site")
    ),
    ActivityType(
      "22030008",
      "activityType.name.fraud-related-private-sec-industry",
      List("SIA", "Bouncer", "Doorman", "Close Protection", "Guard")
    ),
    ActivityType(
      "22030009",
      "activityType.name.fraud-related-income-tax",
      List(
        "ITSA",
        "Working Cash in Hand",
        "Self Employed",
        "Off record",
        "Off book",
        "Money",
        "Back pocket",
        "Sole",
        "Declare",
        "Trader"
      )
    ),
    ActivityType("22030010", "activityType.name.breaking-off-payroll-IR35", List("Contractor", "Employment")),
    ActivityType(
      "22030011",
      "activityType.name.not-registered-self-emp",
      List("Unregistered trader", "Cash in hand", "Backpocket", "Sole", "Trader", "Business", "Trading")
    ),
    ActivityType(
      "22030012",
      "activityType.name.suspicious-cash-dep",
      List("Large cash deposits", "Large increase in cash deposits", "Money", "Finance", "Bank")
    ),
    ActivityType(
      "22030013",
      "activityType.name.suspicious-transport",
      List("Money", "Movement", "Transfer", "Bank", "Account", "Deposit")
    ),
    ActivityType(
      "22030014",
      "activityType.name.money-funding-criminal-org",
      List(
        "OCGs",
        "Criminal finance",
        "Organised crime",
        "Crime",
        "Offence",
        "Dirty",
        "Dodgy",
        "POCA",
        "Confiscate",
        "Stash",
        "Hidden"
      )
    ),
    ActivityType(
      "22030015",
      "activityType.name.not-paying-betting-duty",
      List(
        "Gambling",
        "General Betting Duty",
        "GBD",
        "Pool Betting Duty",
        "PBD",
        "Remote gaming duty",
        "RGD",
        "Gamble",
        "Lottery",
        "Raffle"
      )
    ),
    ActivityType(
      "22030016",
      "activityType.name.illegal-flow-money",
      List("Illicit finance", "Laundering", "Transfer", "Dirty", "Move", "Gang", "Criminal", "Stash")
    ),
    ActivityType(
      "22030017",
      "activityType.name.money-laundering",
      List(
        "Offshore",
        "Transfer",
        "Accounts",
        "Tax Haven",
        "Hawala",
        "Money Services",
        "Money Bureau",
        "Bureau",
        "Bureau De Change",
        "Clean",
        "Dirty",
        "Proceeds",
        "POCA"
      )
    ),
    ActivityType(
      "22030018",
      "activityType.name.not-paying-capital-gains-tax",
      List("Profit", "House Sale", "Property", "Home", "Sold", "Estate", "Auction", "Value")
    ),
    ActivityType(
      "22030019",
      "activityType.name.offshore-bank-accounts",
      List(
        "Not declaring offshore income",
        "Off-shore",
        "Off shore",
        "Dubai",
        "BVI",
        "Panama",
        "British Virgin Islands",
        "Swiss",
        "Transfer",
        "Jersey",
        "Divert"
      )
    ),
    ActivityType(
      "22030020",
      "activityType.name.more-than-two-million-assets-not-paying-tax",
      List(
        "Wealthy individuals",
        "Famous",
        "Well Known",
        "Rich",
        "Professional",
        "Media",
        "Well off",
        "Minted",
        "Sports",
        "Football",
        "TV",
        "Celebrity"
      )
    ),
    ActivityType(
      "22030021",
      "activityType.name.not-paying-inheritance-tax",
      List("Dead", "Death", "Will", "Family", "Estate")
    ),
    ActivityType(
      "22030022",
      "activityType.name.fraud-related-charities",
      List("Charity", "Donation", "Mispresentation", "Goodwill", "Gift Aid", "Relief")
    ),
    ActivityType(
      "22030023",
      "activityType.name.not-paying-duty-on-alcohol",
      List(
        "Excise duty",
        "Commodity fraud",
        "Booze",
        "Spirit",
        "Liquor",
        "Beer",
        "Wine",
        "Offie",
        "Off Licence",
        "Corner Shop",
        "Cider",
        "Vodka",
        "Under Counter"
      )
    ),
    ActivityType(
      "22030024",
      "activityType.name.not-paying-duty-on-tobacco",
      List(
        "Excise duty",
        "Commodity fraud",
        "Cigarettes",
        "Cigars",
        "Baccy",
        "Cigs",
        "Ciggy",
        "Tabs",
        "Sleeve",
        "Rolling",
        "Smoke",
        "Under Counter"
      )
    ),
    ActivityType(
      "22030025",
      "activityType.name.illegal-use-of-red-diesel",
      List("Commodity fraud", "Fuel", "Petrol", "Farm", "Agriculture", "Green", "Car", "HGV", "Vehicle", "Van", "Road")
    ),
    ActivityType(
      "22030026",
      "activityType.name.not-paying-sugar-ax",
      List(
        "Soft Drinks Industry Levy",
        "SDIL",
        "Soft drinks",
        "Commodity fraud",
        "Fizz",
        "Carbonated",
        "Cola",
        "Sweet",
        "Drink",
        "Chocolate",
        "Confectionery"
      )
    ),
    ActivityType(
      "22030027",
      "activityType.name.false-tax-credit-claims",
      List("Child Tax Credit", "Working Tax Credit", "Tax Credit", "Benefit fraud", "Work", "Partner", "Fraud")
    ),
    ActivityType(
      "22030028",
      "activityType.name.false-child-benfit-claims",
      List("CSA", "CMS", "Maintenance", "Kid", "Education", "School", "Benefit Fraud")
    ),
    ActivityType(
      "22030029",
      "activityType.name.fraud-related-to-brexit",
      List("EU", "Rules", "Document", "Europe", "Border", "Movement", "Ireland", "Irish", "Port", "Transport", "HGV")
    ),
    ActivityType(
      "22030030",
      "activityType.name.fraud-related-to-import-and-export",
      List(
        "CITEX",
        "Customs International Trade and Excise",
        "Livestock",
        "Transport",
        "Carnet",
        "TIR",
        "Community Transit",
        "Customs",
        "Border",
        "Courier",
        "Goods",
        "Agent",
        "Shipping",
        "Van"
      )
    ),
    ActivityType(
      "22030031",
      "activityType.name.false-vat-refund-claims",
      List("VAT Repayment Fraud", "Loan Repayment")
    ),
    ActivityType(
      "22030032",
      "activityType.name.not-paying-tax-when-winding-up-company",
      List("Phoenixism", "Close", "Fold", "Closure", "Shutdown", "Open", "Redundant", "Company")
    ),
    ActivityType(
      "22030033",
      "activityType.name.importing-goods",
      List("Carousel fraud", "MTIC fraud", "Missing trader fraud", "Movement", "Warehouse", "HGV")
    ),
    ActivityType(
      "22030034",
      "activityType.name.suspicious-hmrc-emails-text-messages-phone",
      List("Phishing", "Scam", "Cyber", "WhatsApp", "Unknown", "Telecomms", "Call centre", "Fraud", "Fake", "Con")
    ),
    ActivityType(
      "22030035",
      "activityType.name.not-being-paid-minimum-wage",
      List(
        "Underpayment",
        "Underpaid",
        "Work rights",
        "Employment agency",
        "Agriculture",
        "Farming",
        "Working time limits",
        "Earnings",
        "Salary",
        "Exploitation",
        "Swindle",
        "Corrupt",
        "boss",
        "National",
        "Slave",
        "Labour",
        "Paid",
        "NMW"
      )
    ),
    ActivityType(
      "22030036",
      "activityType.name.activity-related-drugs",
      List(
        "Heroin",
        "Cocaine",
        "Weed",
        "Marijuana",
        "Opioids",
        "Cannabis",
        "Meth",
        "Amphetamines",
        "Ecstacy",
        "MDMA",
        "Pills",
        "LSD",
        "Speed",
        "Narcotics",
        "Dope",
        "Crack",
        "Ketamine",
        "Mushrooms"
      )
    ),
    ActivityType("22030037", "activityType.name.smuggling", List("Smuggle", "People", "Goods")),
    ActivityType(
      "22030038",
      "activityType.name.benefit-fraud",
      List("Universal Credit", "Claiming", "Disability", "Carer", "Savings", "Abroad", "Living alone", "Working")
    ),
    ActivityType(
      "22030039",
      "activityType.name.universal-credit-fraud",
      List("Benefit", "Claiming", "Disability", "Carer", "Savings", "Abroad", "Living alone", "Working")
    ),
    ActivityType("22030040", "activityType.name.human-trafficking", List("Human", "People", "Harbouring", "Exploit")),
    ActivityType(
      "22030041",
      "activityType.name.illegal-immigration",
      List("Immigrant", "Illegal", "Refugee", "Asylum", "Seeker", "Migrant")
    ),
    ActivityType("22030042", "activityType.name.border-crime", List("Port", "Ferry", "Plane", "Force"))
  )

}
