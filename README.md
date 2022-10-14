# Previousbike
Previousbike is a free and open source Android app for the bike sharing systems operated by [nextbike](https://www.nextbike.net/). The focus of this app is on simplicity and low resource usage.

It is currently in alpha, with many essential features missing. Use at your own risk.

# Roadmap
- [x] Basic map
    - [x] Show bikes on map
    - [x] Gray out unavailable bikes
    - [ ] Use appropriate icons depending on zoom level to keep the map clean
    - [x] Dynamically request available bikes depending on zoom level, map location, and user location
        - [x] Dynamically remove bikes from the map when zooming out far enough
    - [ ] Let users rent bikes from the map
- [ ] Accounts
    - [x] Implement account login
    - [ ] Let user view account information
        - [ ] Status of currently rented bikes
        - [ ] History of rented bikes
        - [ ] General account info
- [ ] Renting bikes
    - [ ] Allow renting bikes by entering the id
    - [ ] Allow renting bikes by scanning the QR code
    - [ ] Allow renting multiple bikes simultaneously
    - [ ] Allow giving back rented bikes
