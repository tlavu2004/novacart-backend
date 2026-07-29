# 001 — User, Address, and Order Boundaries

## Status

Accepted.

## Decision

Build the **User & Address** module before the Order module.

- **User** is the customer profile and ownership boundary for personal contact data in the current MVP scope. Authentication and authorization credentials remain a separate future concern.
- **Address** belongs to User. A User may manage multiple addresses and may select at most one default address.
- **Order** will store a delivery-address snapshot at creation time rather than relying only on a mutable Address reference.

## Context

Order creation requires a customer identity, contact information, and a delivery address. Allowing Order to directly depend on mutable User/Address records would make historical orders change when a customer edits or deletes an address.

## Consequences

- User & Address establishes the ownership, validation, and lifecycle rules required by Order.
- Order creation copies the selected Address fields into an Order-owned value/snapshot model.
- Address changes or deletion do not alter the delivery information of existing orders.
- The Order module may reference User identity for ownership, but delivery data is read from its snapshot.
- Authentication, authorization, and role rules are deferred until their requirements are defined.

## Initial rules to validate during implementation

- A User owns its addresses; another User cannot read or modify them.
- The default address must belong to the User and be active.
- Deleting the default address requires choosing another default address or clearing the default state according to the final UX rule.
- An Address used by an existing Order remains independently represented by the Order snapshot.
