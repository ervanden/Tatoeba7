# Tatoeba7


building a language trainer based on the tatoeba database.

Status

Reads sentences.csv and link.csv and create sentence groups (same meaning)
Let user select SOURCE languages and TARGET languages
SOURCE languages are languages you are familiar with
TARGET language is the language you want to learn
Clusters are created of sentences with the same meaning
At least one SOURCE and one TARGET language must be in each sentence cluster.
A limited working set is randomly chosen (20 clusters)
Tool will present random SOURCE sentence, user translates for himself, can then verify against the TARGET sentences

next

- let user chose working set size
- let user replace a cluster with another one (because too difficult or too easy)
