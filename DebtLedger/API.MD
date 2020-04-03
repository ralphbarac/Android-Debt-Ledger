# Notes
- Host: ec2-18-191-166-106.us-east-2.compute.amazonaws.com
- All requests return JSON Arrays, even when returning errors
- If experiencing undocumented error, requests will return a JSON Array containing a JSON Object with field "error": "`<error>`"

## Users

- Login: /login/`<email>`/`<password>`/
  - If user found, returns JSON Array containing JSON Object with user fields (except password)
  - If user not found, returns JSON Array containing JSON Object with field "missing": "user not found"

- Get user by id: /user/id/`<id>`/
  - If user found, returns JSON Array containing JSON Object with user fields (except password)
  - If user not found, returns JSON Array containing JSON Object with field "missing": "user not found"

- Get user by email: /user/email/`<email>`/
  - If user found, returns JSON Array containing JSON Object with user fields (except password)
  - If user not found, returns JSON Array containing JSON Object with field "missing": "user not found"

- Add user: /add/`<input>`/
  - `<input>` is a JSON Object containing first_name, last_name, email, and password
  - If user added, returns JSON Array containing JSON Object with user fields (except password)
  - If user not added, returns JSON Array containing JSON Object with field "failure": "user not added"

- Update user: /user/update/`<input>`/
  - `<input>` is a JSON array containing id, first_name, last_name, email, password
  - If user updated, returns JSON Array containing JSON Object with user fields (except password)
  - If user not updated, returns JSON Array containing JSON Object with field "failure": "user not updated"

- Delete user: /user/delete/`<id>`/
  - If user deleted, returns JSON Array containing JSON Object with field "success": "user deleted"
  - If user not deleted, returns JSON Array containing JSON Object with field "failure": "user not deleted"

## Transactions

- Get transaction by id: /transaction/id/`<id>`/
  - If transaction found, returns JSON Array containing JSON Object with transaction fields
  - If transaction not found, returns JSON Array containing JSON Object with field "missing": "transaction not found"

- Get all debts: /transaction/debtor/`<id>`/
  - If transactions found, returns JSON Array containing JSON Objects with transaction fields
  - If transactions not found, returns JSON Array containing JSON Object with field "empty": "no transactions found"

- Get all credits: /transaction/creditor/`<id>`/
  - If transactions found, returns JSON Array containing JSON Objects with transaction fields
  - If transactions not found, returns JSON Array containing JSON Object with field "empty": "no transactions found"

- Get transactions with contact: /transaction/contact/`<user id>`/`<contact id>`/
  - If transactions found, return JSON Array containing JSON Objects with transaction fields
  - If transactions not found, returns JSON Array containing JSON Object with field "empty": "no transactions found"

- Add transaction: /transaction/add/`<input>`/
  - `<input>` is a JSON Object containing debtor (id), creditor (id), description (optional), and amount
  - If transaction added, returns JSON Array containing JSON Object with transaction fields
  - If transaction not added, returns JSON Array containing JSON Object with field "failure": "transaction not added"

- Add multiple transactions: /transaction/addmultiple/`<input>`/
  - `<input>` is a JSON Array containing JSON Objects with debtor (id), creditor (id), description (optional), and amount
  - If transactions added, returns JSON Array containing JSON Object with field "success": "transactions added"
  - If transactions not added, returns JSON Array containing JSON Object with field "failure": "transactions not added"

- Get balances for all contacts of user: /transaction/balances/`<id>`/
  - If contacts found, returns JSON Array containing JSON Objects with id, first_name, last_name, email, and balance
  - If no contacts found, returns JSON Array containing JSON Object with field "empty": "no contacts found"

## Contacts

- Get all contacts of user: /contact/id/`<id>`/
  - If contacts found, returns JSON Array containing JSON Objects with id, first_name, last_name, and email
  - If no contacts found, returns JSON Array containing JSON Object with field "empty": "no contacts found"

- Delete contact: /contact/delete/`<user id>`/`<contact id>`/
  - If contact deleted, returns JSON Array containing JSON Object with field "success": "contacts deleted"
  - If contact not deleted, returns JSON Array containing JSON Object with field "failure": "contacts not deleted"

## Contact Requests

- Add a contact request: /contact_request/add/`<user id>`/`<contact id>`/
  - If contact request added, returns JSON Array containing JSON Object with contact request fields (status is "pending")
  - If contact request not added, returns JSON Array containing JSON Object with field "failure": "contact request not added"

- Get pending contact requests (for user): /contact_request/pending/`<id>`/
  - If pending contact requests found, returns JSON Array containing JSON Objects with id, first_name, last_name, and email
  - If pending contact requests not found, returns JSON Array containing JSON Object with field "empty": "no requests found"

- Get pending contact requests (from user): /contact_request/pendingfrom/`<id>`/
  - If pending contact requests found, returns JSON Array containing JSON Objects with id, first_name, last_name, and email
  - If pending contact requests not found, returns JSON Array containing JSON Object with field "empty": "no requests found"

- Accept a request: /contact_request/accept/`<user id>`/`<contact id>`/
  - If contact request accepted, returns JSON Array containing JSON Object with contact request fields (status is "accepted")
  - If contact request not accepted, returns JSON Array containing JSON Object with field "failure": "request not found"
  - Note: also creates associated contact table entries for both user and contact

- Deny a request: /contact_request/deny/`<user id>`/`<contact id>`/
  - If contact request denied, returns JSON Array containing JSON Object with field "success": "request denied"
  - If contact request not denied, returns JSON Array containing JSON Object with field "failure": "request not found"

- Delete a request: /contact_request/delete/`<user id>`/`<contact id>`/
  - If contact request deleted, returns JSON Array containing JSON Object with field "success": "contact request deleted"
  - If contact request not deleted, returns JSON Array containing JSON Object with field "failure": "contact request not deleted"

## Contact Groups

- Get all groups of user: /contact_group/grouplist/`<user id>`/
  - If groups found, returns JSON Array containing JSON Objects with group id, name, and members (JSON Array containing JSON Objects with user fields)
  - If groups not found, returns JSON Array containing JSON Object with field "empty": "no groups found"

- Get group by id: /contact_group/group/`<id>`/
  - If group found, returns JSON Array containing JSON Object with group fields
  - If group not found, returns JSON Array containing JSON Object with field "missing": "group not found"

- Add group: /contact_group/add/`<input>`/
  - `<input>` is a JSON Object containing owner (id) and name
  - If group added, returns JSON Array containing JSON Object with group fields
  - If group not added, returns JSON Array containing JSON Object with field "failure": "group not added"

- Update user: /contact_group/update/`<id>`/`<name>`/
  - If group updated, returns JSON Array containing JSON Object with field "success": "group updated"
  - If group not updated, returns JSON Array containing JSON Object with field "failure": "group not updated"

- Delete group: /contact_group/delete/`<id>`/
  - If group deleted, returns JSON Array containing JSON Object with field "success": "group deleted"
  - If group not deleted, returns JSON Array containing JSON Object with field "failure": "group not deleted"

## Contact Group Members

- Get list of all members of a group: /contact_group_member/memberlist/`<id>`/
  - If members found, returns JSON Array containing JSON Objects with id, first_name, last_name, and email
  - If members not found, returns JSON Array containing JSON Object with field "empty": "no members found"

- Set members: /contact_group_member/set/`<input>`/
  - `<input>` is a JSON Object containing group_id and members, where members is a JSON Array containing members (id) (e.g. {"group_id":1,"members":[2, 3, 6]})
  - If member added, returns JSON Array containing JSON Object with field "success": "members added"
  - If member not added, returns JSON Array containing JSON Object with field "failure": "members not added"

- Add a member: /contact_group_member/add/`<group id>`/`<user id>`/
  - If member added, returns JSON Array containing JSON Object contact group member fields
  - If member not added, returns JSON Array containing JSON Object with field "failure": "member not added"

- Remove a member: /contact_group_member/remove/`<group id>`/`<user id>`/
  - If member removed, returns JSON Array containing JSON Object with field "success": "member removed"
  - If member not removed, returns JSON Array containing JSON Object with field "failure": "member not removed"
