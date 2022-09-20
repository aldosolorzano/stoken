(ns playground
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application.

  Call `(reset)` to reload modified code and (re)start the system.

  The system under development is `system`, referred from
  `com.stuartsierra.component.repl/system`.

  See also https://github.com/stuartsierra/component.repl"
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer [javadoc]]
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.set :as set]
   [clojure.string :as string]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer [clear refresh refresh-all]]
   [com.stuartsierra.component :as component]
   [com.stuartsierra.component.repl :refer [reset stop]]
   [crypto.random :as rand]
   [io.stokes.block :as block]
   [io.stokes.hash :as hash]
   [io.stokes.key :as key]
   [io.stokes.key-store :as key-store]
   [io.stokes.miner :as miner]
   [io.stokes.node :as node]
   [io.stokes.p2p :as p2p]
   [io.stokes.queue :as queue]
   [io.stokes.rpc :as rpc]
   [io.stokes.state :as state]
   [io.stokes.transaction :as transaction]
   [io.stokes.transaction-pool :as transaction-pool]
   [me.raynes.fs :as fs]
   [dev :as dev]))

(def pp pprint)

;; Do not try to load source code from 'resources' directory
(clojure.tools.namespace.repl/set-refresh-dirs "dev" "src" "test")

;; TODO first excercise will be about reading the info of the blocks and understanding if it's healthy or not.
;; TODO Build a transaction
;; TODO Broadcast transaction and validate that all the other nodes are in sync
;; TODO Behind the scenes of a miner
;; TODO how can we connect my blockchain with others running on their machines?

;; A node has this schema. Only putting relevant values for the excercise
(comment
  {:blockchain       {:initial-state genesis-block}
   :transaction-pool {:initial-state transactions}
   :ledger           {:initial-state (:transactions genesis-block)}})

(comment
  (def BlockInternal
    {:block {:transactions     [{:inputs  [{:type s/keyword :block-height s/Int}]
                                 :outputs [{:type   s/Keyword
                                            :value  s/Int
                                            :script {:script-type s/Keyword
                                                     :address     s/Str
                                                     :hash        s/Str
                                                     :index       s/Int}}]}]
             :transaction-root {:schema s/Str :doc "The merkle root is derived from the hashes of all transactions included in this block" :required true}
             :time             {:schema DateTime :doc "Timestamp when the block was mined" :required true}
             :nonce            {:schema s/Int :doc "" :required true}
             :previous-hash    {:schema s/Str :doc "previous block hash, nil in case of genesis block" :required false}
             :hash             {:schema s/Str :doc "block hash, unique identifier" :required true}
             :difficulty       {:schema s/Int :doc "difficulty level of the block" :required true}
             :children         {:schema [{:block BlockInternal} :required false]}}}))


;; STEP 0 Start the system to solve the exercises
(comment (dev/ensure-system-started!))

;; 1. Get the height of a radnom block. Check the same block height in a different node. Are they equal?

;; 2. `dev/system` var contains all the nodes in the network, each one has their own blockchain.
;; - Verify that each node contains the blockchain with the same length
;; - Verify that each node contains the same genesis hash
;; - Verify that each node contains the same head's (last block in the chain) hash
;; - Verify that the chainwalk is consistent. Previous hash form the next block is equal to the current block

;; 3. Build a trasaction

;; 4. Broadcast the transaction

;; 5. Find the transaction in the lates block.
;; - Which block height was mined ?
;; - Find the tx in a different node, is the block the same ?
;; - Apply the same rules as excercise 2
