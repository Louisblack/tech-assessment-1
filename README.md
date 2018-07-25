### Louis' Wonderful ATM

Java application for the contract technical test.

To build and run tests just run `./gradlew check --info`

No specific tests for `AtmServiceImpl` as it's just orchestrating other services so an 
Integration Test is included to test all the classes working together.

I've synchronised access to the ATM withdrawal logic so that the actions of withdrawing
money from the user account and the ATM's cash box are an atomic action. In a proper situation
I would imagine this would be done in a transaction so we could handle things like roll back.
 