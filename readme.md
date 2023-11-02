# WIP

# Expand Contract

** WARNING**

EXPERIMENTAL WORK IN PROGRESS DO NOT USE

Expand contract is a way of handling database schema migrations.
Suppose that we have a service at version "X" that works with a DDL "a"
and we want to migrate the service to the version "Y" that works with a DDL "b"

in the first phase (Expand) only the "A" code is running "pointing to a "schema" that
behaves like "a". In the meanwhile a new "schema" is going to be prepared
that will be able to behave like "b".
Once this new schema is ready, the system goes in "transition" state and we can deploy "Y".
during transition "X" and "Y" are running toghether, using their own schemas.
When the last instance of "X" goes down, we can start the transition to the "contract"
phase, in which the "a" schema is "removed" and the new "b" schema is the only used.
Once the contract phase ends, we are agein in Business as usual state.

# tasks


## uberjar

> clojure -T:build uberjar :project command-line

## native image

graalvm must be available)

> clojure -T:build native :project command-line

## clean
> clojure -T:build clean :project command-line



# Polylith

> clojure -M:poly shell

This project is a polylith project
<img src="logo.png" width="30%" alt="Polylith" id="logo">

The Polylith documentation can be found here:

- The [high-level documentation](https://polylith.gitbook.io/polylith)
- The [Polylith Tool documentation](https://polylith.gitbook.io/polylith/poly)
- The [RealWorld example app documentation](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app)

You can also get in touch with the Polylith Team on [Slack](https://clojurians.slack.com/archives/C013B7MQHJQ).


## test
WRITE ALLOTAHTHEM
