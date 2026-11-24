
# API and Reporting

## Resend Email API Integration

RentEase integrates the Resend Email API to send rental confirmation emails.

Features:
- Retrieve the API key from a Windows environment variable
- Send email requests using HTTP POST
- Include rental information in confirmation emails
- Handle API errors separately from rental database transactions

## Reports Management

The Reports module provides access to business records.

Available reports:
- Rental Transactions
- Payment Records
- Equipment Inventory
- Customer List
- Returns and Late Fees

## Database Integration

The reports retrieve information from the MySQL database.

Related database tables:
- rentals
- rental_details
- payments
- equipment
- categories
- customers
- returns

## Security

The Resend API key is stored outside the source code using an environment variable named RESEND_API_KEY.
