# Avoor Android app
Explore time with AI.

## Setup
Avoor should work out of the box. However, there are a few parameters that you might need to change.

### Server URL
There are 2 server URLs - one for production and one for development. The server API to use is controlled by the `production` variable in the `AvoorApplication` constructor.

To change the server URL, change `LOCAL_URL` (development) or `BASE_URL` (production) in both `RetrofitInstance` and `SocketIOInstance`.

> If a server URL doesn't support HTTPS, add it to `network_security_policy.xml` too.

## Demo mode
Demo mode shows fake data. Namely:
- The login screen works with any email and password (8 characters or more). Registration may not work correctly.
- The calendar shows a mock list of events.
- Data won't be saved.

To enable demo mode, set the `demo` variable in the `AvoorApplication` constructor to true.
