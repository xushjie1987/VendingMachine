# The valid set of actions on the vending machine are:

* NICKEL(0.05), DIME(0.10), QUARTER(0.25), DOLLAR(1.00) - insert money
* COIN RETURN - returns all inserted money
* GET-A, GET-B, GET-C - select item A ($0.65), B ($1), or C ($1.50)
* SERVICE - a service person opens the machine and sets the available change and items

#The valid set of responses from the vending machine are:

* NICKEL, DIME, QUARTER - return coin
* A, B, C - vend item A, B, or C

#The vending machine must track the following state:

* available items - each item has a count, a price, and a selector (A,B,or C)
* available change - # of nickels, dimes, quarters, and dollars available
currently inserted money

#Example input and output

\```
Example 1: Buy B with exact change
Q, Q, Q, Q, GET-B
-> B

Example 2: Start adding change but hit coin return to get change back
Q, Q, COIN-RETURN
-> Q, Q

Example 3: Buy A without exact change (return $.35)
DOLLAR, GET-A
-> A, Q, D

```

