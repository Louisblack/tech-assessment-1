### Louis' Wonderful ATM

Java application for the contract technical test.

To build and run tests just run `./gradlew check --info`

No specific tests for `AtmServiceImpl` as it's just orchestrating other services so an 
Integration Test is included to test all the classes working together.

I've synchronised access to the ATM withdrawal logic so that the actions of withdrawing
money from the user account and the ATM's cash box are an atomic action. In a proper situation
I would imagine this would be done in a transaction so we could handle things like roll back.

The `AccountServiceImpl` has an `AccountRepository` dependency. For the sake of testing I have 
provided a `FakeAccountRepository` that can be set up with the relevant accounts but could be
swapped out for a proper one without any code changes.

In the integration test we wire everything up manually but it'd be simple to use a DI 
framework.

Currency denominations are defined as an enum `Note`. 
The ATMService interface works by replenishing the ATM with notes in a `Map<Note, Long>` so
denomination to the number of that note. 

Withdrawals can then be made by providing an amount in pence and an account number which 
will return a `Withdrawal` object containing either a `Map<Note, Long>` with the notes
and amount of each note dispensed or an error message if something failed along the way. 