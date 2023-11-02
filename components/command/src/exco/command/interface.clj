(ns exco.command.interface
  (:require [exco.command.core :as core]
            [exco.command.spec :as spec]))

(defn valid?
  [cmd]
  (core/valid? cmd))

(defn validate!
  [cmd]
  (core/validate! cmd))

(defn parse
  [lst]
  (core/parse lst))

(def spec-of spec/action)

(def actions spec/actions)
