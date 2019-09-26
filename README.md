# Multi-threaded client/server chat system
1st mandatory assignment - 3rd semester (This is a school assignment).

Multi-threaded client/server chat system.

### Protocol
Message type | Message direction | Description
------------ | ----------------- | -------------
JOIN <<user_name>>, <<server_ip>>:<<server_port>> | Client to server | The user name is given by the user. Username is max 12 chars long, only letters, digits, ‘-‘ and ‘_’ allowed.
J_OK | Server to client | Client is accepted.
J_ER <<err_code>>: <<err_msg>> | Server to client | Client not accepted. Duplicate username, unknown command, bad command or any other errors.
DATA <<user_name>>: <<free text…>> | Client to server | From server to all clients. First part of message indicates from which user it is, the colon(:) indicates where the user message begins. Max 250 user characters.
IMAV | Client to server | Client sends this heartbeat alive every 1 minute.
QUIT | Client to server | Client is closing down and leaving the group
LIST <<name1 name2 name3 …>> | Server to client | A list of all active user names is sent to all clients, each time the list at the server changes.

### Links
* [JavaDoc](https://github.com/andreasdan/Chat-system/tree/master/doc/JavaDoc/ "JavaDoc")
* [Rapport](https://github.com/andreasdan/Chat-system/tree/master/doc/Rapport.pdf "Rapport")
* [Kode](https://github.com/andreasdan/Chat-system/tree/master/src/kea/chatsystem/ "Kode")