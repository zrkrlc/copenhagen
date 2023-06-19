(ns copenhagen.story
  (:require [reagent.core :as reagent]))

;; --------------------------------
;; Story graph
;; --------------------------------

;;; Constants

;; Contains all story elements
(defonce *registry (reagent/atom {}))


(def ^:private hierarchy [:element.type/story
                          :element.type/paragraph
                          :element.type/sentence
                          :element.type/word])


;;; Utilities

(defn valid-relationship?
  "Checks if two elements can have a parent-child relationship via their types"
  [parent-type child-type]
  (assert ((set hierarchy) parent-type) "Parent type not found")
  (assert ((set hierarchy) child-type) "Child type not found") 
  
  (= (.indexOf hierarchy parent-type) 
     (dec (.indexOf hierarchy child-type))))


;; TODO: implement this
(defn has-cycle?
  "Detects if story graph will have a cycle if child is added to it"
  [registry child-element]
  false)


(defn valid-child?
  "Checks if a potential child element can be added to the registry"
  [registry parent-element child-element]
  (and (valid-relationship? (:element/type parent-element)
                        (:element/type child-element))
       (not (has-cycle? registry child-element))))


;;; Constructor

(defn element 
  ([registry] (element registry {}))

  ([registry {:element/keys [id type content children dispatch triggered? active?]
              :or            {id         (random-uuid)
                              type       :element.type/word
                              content    ""
                              children   []
                              dispatch   nil
                              triggered? false
                              active?    false}}]

   #:element{:id         id
             :type       type
             :content    (if (string? content) [:span content] content)
             :children   (vec (filter (fn [child]
                                        (valid-child?
                                         registry
                                         (element registry #:element{:type type})
                                         child)) children))
             :dispatch   dispatch
             :triggered? triggered?
             :active?    active?}))


;;; Operations

(defn root
  "Gets the root node from the registry"
  [registry]
  (let [results (filter #(= (:element/type %) :element.type/story) (vals registry))]
    (assert (= (count results) 1) "Non-unique story node")
    (first results)))


(defn ^:private path-traverse
  [registry current-path current-node target]
  (let [current-element (get registry current-node)]
    (if (= (:element/id current-element) target)
      current-path
      (some (fn [[idx child]]
              (when-let [p (path-traverse
                            registry
                            (conj current-path idx)
                            child
                            target)]
                (seq p)))
            (map-indexed vector (:element/children current-element))))))


(defn path
  "Given a target, returns its path vector from the (optional) root if it exists;
   otherwise, return nil"
  [registry target]
  (let [root* (:element/id (root registry))]
    (if-some [path (path-traverse registry [root*] root* target)] (vec path) nil)))


;; Ex:
;; (walk registry [0]) -> returns the root node
;; (walk registry [0 1]) -> returns element with ID 2
(defn walk
  [registry path]
  "Given a path, returns the element associated with that path")


(defn assoc-content
  [registry target content]
  "Assocs content to the element corresponding to the target ID and returns the new registry")


(defn dissoc-content
  [registry target content]
  "Dissocs content to the element corresponding to the target ID and returns the new registry")


;; Ex. (upsert registry [0 2] (element registry #element{:id 3}))
;; => inserts new element with ID 3 at path [0 2], which makes it a new child of the root node
(defn upsert
  [registry path element]
  "Inserts or updates the element at a particular path, if it exists, and returns 
   a new registry; otherwise, recursively creates the parent elements so element
   can be inserted")


;; Ex. (graph registry)
;; => [:div.story {:id 0} [:p.paragraph {:id 1} [:span.sentence {:id :a1} "This is a sentence"]]] ...]
(defn graph
  [registry]
  "Returns Hiccup of the entire story graph, where each vector's ID attribute is
   the ID of its corresponding element, and each vector's innerHTML is the 
   element's content.
   
   The HTML element and classes used should depend on :element/type.
   
   For :element.type/story, it should be a div with class story
   For :element.type/paragraph, it should be a p with class paragraph
   For :element.type/sentence or :element.type/word, it should be a span with 
   class sentence or word")

;; --------------------------------
;; REPL
;; --------------------------------

(comment
  ;; Sets up initial story graph
  (do (reset! *registry {})
      (swap! *registry assoc :a (element @*registry #:element{:id         :a
                                                              :type       :element.type/story
                                                              :content    ""
                                                              :children   [:aa :ab]
                                                              :dispatch   nil
                                                              :triggered? false
                                                              :active?    false}))

      (swap! *registry assoc :aa (element @*registry #:element{:id         :aa
                                                               :type       :element.type/paragraph
                                                               :content    ""
                                                               :children   [:aaa :aab :aac]
                                                               :dispatch   nil
                                                               :triggered? false
                                                               :active?    false}))

      (swap! *registry assoc :ab (element @*registry #:element{:id         :ab
                                                               :type       :element.type/paragraph
                                                               :content    ""
                                                               :children   [:aba :abb :abc]
                                                               :dispatch   nil
                                                               :triggered? false
                                                               :active?    false}))

      (doall (for [key [:aaa :aab :aac :aba :abb :abc]]
               (swap! *registry assoc key (element @*registry #:element{:id         key
                                                                        :type       :element.type/sentence
                                                                        :content    ""
                                                                        :children   []
                                                                        :dispatch   nil
                                                                        :triggered? false
                                                                        :active?    false})))))
  
  ;; Introduces a cycle; should be detected
  (swap! *registry assoc :aad (element @*registry #:element{:id         :aad
                                                            :type       :element.type/paragraph
                                                            :content    ""
                                                            :children   [:a]
                                                            :dispatch   nil
                                                            :triggered? false
                                                            :active?    false}))
  
  (println (map :element/type (vals @*registry)))

  (println @*registry)
  )