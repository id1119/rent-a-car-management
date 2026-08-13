# Rent-a-Car Management System

A Java 17 desktop information system for managing the core workflows of a rent-a-car agency. The project uses a Swing GUI with separate Administrator, Agent and Client interfaces, a service layer for business rules, repositories for persistence and automated service tests.

## Highlights

- Role-specific Swing interfaces for administrators, agents and clients
- Vehicle categories, vehicle models and individual physical vehicles
- Reservation workflow with pending, approved, rejected and canceled states
- Date-aware vehicle availability across overlapping reservations
- Client eligibility checks before reservation creation
- Subscription-based reservation access
- Temporary reservation blocking after client cancellation
- Pricing by vehicle category with client discounts and extra services
- Configurable rental duration and extended-use pricing
- Vehicle issue and return workflow with mileage tracking
- Late-return penalty calculation
- Operational and financial reporting
- XChart visualizations for revenue, reservation status and agent workload
- CSV-oriented repository layer
- Automated service test suite

## Roles

### Administrator
Manages agency-wide data and configuration and has access to operational and financial reporting.

### Agent
Processes reservations, manages clients and subscriptions, issues vehicles and records vehicle returns.

### Client
Browses availability, creates reservation requests and manages personal reservation workflows.

## Reservation logic

Reservations are created for a vehicle model. A specific physical vehicle is assigned later when the rental is issued.

Before creating a reservation, the system checks that:

- the requested dates are valid
- the reservation begins no earlier than the next day
- the client meets the driver's-license requirement
- the client has an active subscription
- the client is allowed to make a new reservation
- a vehicle of the requested model is available for the selected period

Availability takes overlapping pending and approved reservations into account. Expired pending reservations can be rejected automatically, and a client cancellation creates a temporary 24-hour reservation block.

## Pricing

The pricing service supports:

- daily rates by vehicle category
- discounts by client category
- additional services
- configurable default rental duration
- extended-use charges
- late-return penalties

## Vehicle issue and return

Only approved reservations can proceed to vehicle issue. The selected physical vehicle must be available and match the reserved model.

The issue workflow records starting mileage, assigns the vehicle to the reservation and changes its status to rented. The return workflow records final mileage, restores availability and calculates a penalty when the vehicle is returned late.

## Reports and dashboards

The project includes reporting logic and an XChart-powered Swing dashboard with visualizations for:

- revenue by client category over time
- reservation-status distribution
- agent workload based on processed reservations

## Architecture

```text
src/
├── main/         # Application entry point
├── gui/          # Swing screens and dashboard charts
├── model/        # Domain entities and enums
├── service/      # Business rules and workflows
├── repository/   # Persistence and data access
└── app/          # Application composition and data loading

test/             # Service test suite
data/             # Application data
lib/              # External libraries
run_tests.sh       # Test runner
```

Main services include:

```text
AuthService
ClientService
ReservationService
PricingService
SubscriptionService
VehicleService
VehicleRentalService
ReportService
```

## Tech stack

- Java 17
- Java Swing
- XChart
- Object-oriented design
- Layered architecture
- CSV persistence
- Automated service testing

## Running

The desktop application starts from:

```text
main.Main
```

Run it from a Java IDE with the libraries from `lib/` on the classpath.

### Tests

On macOS/Linux:

```bash
chmod +x run_tests.sh
./run_tests.sh
```

The script compiles the source and test files and runs `test.ServiceTests`.

## Project context

An Object-Oriented Programming project focused on building a complete information system with domain modeling, role-based workflows, business-rule validation, persistence, reporting, GUI development and automated testing.
