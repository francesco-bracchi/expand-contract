(ns exco.hunk.core
  (:refer-clojure :exclude [commute])
  (:require [clojure.spec.alpha :as s]
            [exco.hunk.spec]))

(defmulti inverse
  "inverse of a hunk, always possible"
  :hunk/action)

(defmethod inverse :action/create-table
  [action]
  (assoc action :hunk/action :action/drop-table))

(defmethod inverse :action/drop-table
  [action]
  (assoc action :hunk/action :action/create-table))

(defmethod inverse :action/rename-table
  [{:hunk/keys [name-from name-to] :as action}]
  (assoc action :name-from name-to :name-to name-from))

(defmethod inverse :action/create-index
  [action]
  (assoc action :hunk/action :action/drop-index))

(defmethod inverse :action/drop-index
  [action]
  (assoc action :hunk/action :action/create-index))

(defmethod inverse :action/add-column
  [action]
  (assoc action :hunk/action :action/drop-column))

(defmethod inverse :action/drop-column
  [action]
  (assoc action :hunk/action :action/add-column))

(defmethod inverse :action/rename-column
  [{:hunk/keys [name-from name-to] :as action}]
  (assoc action :name-from name-to :name-to name-from))

(defmethod inverse :action/alter-column
  [{:hunk/keys [column-from column-to] :as action}]
  (assoc action :column-from column-to :column-to column-from))

(defmulti coalesce
  "coalesce if possible 2 hunks. It can annihilate."
  #(vec (map :hunk/action %&)))

(defmethod coalesce :default
  [_ _]
  nil)

(defmethod coalesce [:action/create-table :action/drop-table]
  [p q]
  (when (= (:hunk/table-name p) (:hunk/table-name q))
    :hunk/nihil))

(defmethod coalesce [:action/create-table :action/rename-table]
  [p q]
  (when (= (:hunk/table-name p) (:hunk/name-from q))
    (assoc p :hunk/table-name (:hunk/name-to q))))

(defmethod coalesce [:action/create-table :action/add-column]
  [p q]
  (when (= (:hunk/table-name p) (:hunk/table-name q))
    (update p :hunk/columns assoc (:hunk/column-name q) (:hunk/column q))))

(defmethod coalesce [:action/create-table :action/drop-column]
  [p q]
  (when (= (:hunk/table-name p) (:hunk/table-name q))
    (update p :hunk/columns dissoc (:hunk/column-name q))))

(defn rename-key
  [map from to]
  (-> map
      (dissoc from)
      (assoc to (map from))))

(defmethod coalesce [:action/create-table :action/rename-column]
  [p q]
  (when (= (:hunk/table-name p) (:hunk/table-name q))
    (update p :hunk/columns rename-key (:hunk/name-from q) (:hunk/name-to q))))

(defmethod coalesce [:action/create-table :action/alter-column]
  [p q]
  (when (= (:hunk/table-name p) (:hunk/table-name q))
    (update p :hunk/columns assoc (:hunk/column-name q) (:hunk/column q))))

(defmethod coalesce [:action/rename-table :action/drop-table]
  [p q]
  (when (= (:hunk/name-to p) (:hunk/table-name q))
    (assoc q :hunk/table-name (:hunk/name-from p))))

(defmethod coalesce [:action/rename-table :action/rename-table]
  [p q]
  (when (= (:hunk/name-to p) (:hunk/name-from q))
    (assoc p :hunk/name-to (:hunk/name-to q))))

(defmethod coalesce [:action/create-index :action/drop-index]
  [p q]
  (when (= (:hunk/index-name p) (:hunk/index-name q))
    :hunk/nihil))

(defmethod coalesce [:action/add-column :action/drop-column]
  [p q]
  (when (and (= (:hunk/table-name p) (:hunk/table-name q))
             (= (:hunk/column-name p) (:hunk/column-name q)))
    :hunk/nihil))

(defmethod coalesce [:action/add-column :action/rename-column]
  [p q]
  (when (and (= (:hunk/table-name p) (:hunk/table-name q))
             (= (:hunk/column-name p) (:hunk/name-from q)))
    (assoc p :hunk/column-name (:hunk/name-to q))))

(defmethod coalesce [:action/add-column :action/alter-column]
  [p q]
  (when (and (= (:hunk/table-name p) (:hunk/table-name q))
             (= (:hunk/column-name p) (:hunk/column-name q)))
    (assoc p :hunk/column (:hunk/column q))))

(defmethod coalesce [:action/rename-column :action/drop-column]
  [p q]
  (when (and (= (:hunk/table-name p) (:hunk/table-name q))
             (= (:hunk/name-to p) (:hunk/column-name q)))
    (assoc q :hunk/column-name (:hunk/name-from p))))

(defmethod coalesce [:action/rename-column :action/rename-column]
  [p q]
  (when (and (= (:hunk/table-name p) (:hunk/table-name q))
             (= (:hunk/name-to p) (:hunk/name-from q)))
    (assoc p :hunk/name-to (:hunk/name-to q))))

(defmethod coalesce [:action/alter-column :action/alter-column]
  [p q]
  (when (and (= (:hunk/table-name p) (:hunk/table-name q))
             (= (:hunk/column-name p) (:hunk/column-name q)))
    (update p :hunk/column merge (:hunk/column q))))

(defmethod coalesce [:action/drop-column :action/add-column]
  [p q]
  (when (and (= (:hunk/table-name p) (:hunk/table-name q))
             (= (:hunk/column-name p) (:hunk/column-name q)))
    (if (= (:hunk/column p) (:hunk/column q))
      :hunk/nihil
      #:hunk{:action :action/alter-column
             :table-name (:hunk/table-name p)
             :column-name (:hunk/column-name p)
             :column-from (:hunk/column p)
             :column-to (:hunk/column q)})))

(defmethod coalesce [:action/alter-column :action/drop-column]
  [p q]
  (when (and (= (:hunk/table-name p) (:hunk/table-name q))
             (= (:hunk/column-name p) (:hunk/column-name q)))
    #:hunk{:action :action/drop-column
           :table-name (:hunk/table-name p)
           :column-name (:hunk/column-name p)
           :column (:hunk/column-from p)}))

(defn not-same-table
  [p q]
  (when (not= (:hunk/table-name p) (:hunk/table-name q))
    [q p]))

(defn not-same-column
  [p q]
  (when (or (not= (:hunk/table-name p) (:hunk/table-name q))
            (not= (:hunk/column-name p) (:hunk/column-name q)))
    [q p]))

(defn commute-rename-table
  [p q]
  (let [p' (if (= (:hunk/name-to p) (:hunk/table-name q)) (assoc q :hunk/table-name (:hunk/name-from p)) q)
        q' p]
    [p' q']))

(defmulti commute
  "commute the order of 2 hunks. it can fail (like inverting creation an destruction of the same table)
   for truly independent hunks returns the 2 hunks in inverted order, rename can change the values"
  #(vec (map :hunk/action %&)))

(defmethod commute :default
  [p q]
  [q p])

(defmethod commute
  [:action/create-table :action/create-table]
  [p q]
  (when (not= (:hunk/table-name p) (:hunk/table-name q))
    [q p]))

(defmethod commute
  [:action/create-table :action/drop-table]
  [p q]
  (not-same-table p q))

(defmethod commute
  [:action/create-table :action/rename-table]
  [p q]
  (when (not= (:hunk/table-name p) (:hunk/name-from q))
    [q p]))

(defmethod commute
  [:action/create-table :action/add-column]
  [p q]
  (not-same-table p q))

(defmethod commute
  [:action/create-table :action/drop-column]
  [p q]
  (not-same-table p q))

(defmethod commute
  [:action/create-table :action/rename-column]
  [p q]
  (not-same-table p q))

(defmethod commute
  [:action/create-table :action/alter-column]
  [p q]
  (not-same-table p q))

(defmethod commute
  [:action/rename-table :action/drop-table]
  [p q]
  (when (not= (:hunk/name-to p) (:hunk/table-name q))
    [q p]))

(defmethod commute
  [:action/rename-table :action/rename-table]
  [p q]
  (when (not= (:hunk/name-to p) (:hunk/table-from q))
    [q p]))

(defmethod commute
  [:action/rename-table :action/add-column]
  [p q]
  (commute-rename-table p q))

(defmethod commute
  [:action/rename-table :action/drop-column]
  [p q]
  (commute-rename-table p q))

(defmethod commute
  [:action/rename-table :action/rename-column]
  [p q]
  (commute-rename-table p q))

(defmethod commute
  [:action/rename-table :action/alter-column]
  [p q]
  (commute-rename-table p q))

(defmethod commute
  [:action/create-index :action/drop-index]
  [p  q]
  (when (not= (:hunk/index-name p) (:hunk/index-name q))
    [q p]))

(defmethod commute
  [:action/add-column :action/drop-table]
  [p q]
  (not-same-table p q))

(defmethod commute
  [:action/add-column :action/rename-table]
  [p q]
  [q
   (if (= (:hunk/table-name p) (:hunk/name-from q))
     (assoc p :hunk/table-name (:hunk/name-to q))
     p)])

(defmethod commute
  [:action/add-column :action/drop-column]
  [p q]
  (not-same-column p q))

(defmethod commute
  [:action/add-column :action/rename-column]
  [p q]
  (when (or (not= (:hunk/table-name p) (:hunk/table-name q))
            (not= (:hunk/column-name p) (:hunk/name-from q)))
    [q p]))

(defmethod commute
  [:action/add-column :action/alter-column]
  [p q]
  (not-same-column p q))

(defmethod commute
  [:action/drop-column :action/drop-table]
  [p q]
  (not-same-table p q))

(defmethod commute
  [:action/drop-column :action/rename-table]
  [p q]
  [q
   (if (= (:hunk/table-name p) (:hunk/name-from q))
     (assoc p :hunk/table-name (:hunk/name-to q))
     p)])

(defmethod commute
  [:action/rename-column :action/drop-table]
  [p q]
  (not-same-table p q))

(defmethod commute
  [:action/rename-column :action/rename-table]
  [p q]
  [q
   (if (= (:hunk/table-name p) (:hunk/name-from q))
     (assoc p :hunk/table-name (:hunk/name-to q))
     p)])

(defmethod commute
  [:action/rename-column :action/drop-column]
  [p q]
  (when (or (not= (:hunk/table-name p) (:hunk/table-name q))
            (not= (:hunk/name-to p) (:hunk/column-name q)))
    [q p]))

(defmethod commute
  [:action/rename-column :action/rename-column]
  [p q]
  (when (or (not= (:hunk/table-name p) (:hunk/table-name q))
            (not= (:hunk/name-to p) (:hunk/name-from q)))
    [q p]))

(defmethod commute
  [:action/rename-column :action/alter-column]
  [p q]
  [(if (and (= (:hunk/table-name p) (:hunk/table-name q))
            (= (:hunk/name-to p) (:hunk/column-name q)))
     (assoc q :hunk/column-nname (:hunk/name-from p))
     p)])

(s/fdef inverse
  :args (s/+ :hunk/t)
  :ret :hunk/t)

(s/fdef coalesce
  :args (s/cat :p :hunk/t :q :hunk/t)
  :ret (s/or :sc :hunk/t
             :nl :hunk/nihil
             :fl nil))

(s/fdef commute
  :args (s/cat :p :hunk/t :q :hunk/t)
  :ret (s/or :sc (s/cat :q' :hunk/t :p' :hunk/t)
             :fl nil))

(defn valid?
  [p]
  (s/valid? :hunk/t p))

(defn validate!
  [p]
  (when-not (valid? p)
    (throw (ex-info "invalid hunk" {:hunk p})))
  p)
