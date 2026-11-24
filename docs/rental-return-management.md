
# Rental and Return Management

## Rental Management

The Rentals module handles equipment rental transactions.

Features:
- Select registered customers
- Select available equipment
- Add multiple items to the rental cart
- Calculate rental duration and total cost
- Validate equipment availability
- Save rental transactions and rental details
- Deduct rented quantities from available inventory
- Send rental confirmation emails through Resend

## Return Management

The Returns module handles equipment return processing.

Features:
- Display ongoing rentals
- Record actual return dates
- Calculate applicable late fees
- Record equipment condition notes
- Restore equipment availability
- Record the administrator who received the return

## Database Integration

Related database tables:
- rentals
- rental_details
- returns
- equipment
- customers
- users

Rental processing uses database transactions to help maintain data consistency.
