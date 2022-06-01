
package utils

import models.addresslookup.{AddressRecord, Country, ProposedAddress}
import services.{Address => PafAddress}

trait PafFixtures {
  val exampleCountryUK = Country("UK", "United Kingdom")
  val subDivision = Some(Country("GB-ENG", "England"))

  val twoAddress = List(ProposedAddress("GB990091234514",None,None,None,None,
    Some("AA1 1AA"),Some("Anytown"),List("2 Other Place", "Some District"),
    Country("GB","United Kingdom"),None),
    ProposedAddress("GB990091234515",None,None,None,None, Some("AA1 1AA"),
      Some("Anytown"),List("3 Other Place", "Some District"),Country("GB","United Kingdom"),None))

}

