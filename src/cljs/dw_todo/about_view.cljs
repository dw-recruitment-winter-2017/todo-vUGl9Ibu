(ns dw-todo.about-view)

(defn about-page []
  [:div.page-content
   [:h2 "About dw-todo"]
   [:p
    "Submission for the Democracy Works Anonymous Coding Exercise. Base project template is "
    [:a {:href "https://github.com/reagent-project/reagent-template"} "Reagent Template"]]
   [:p
    "This code is deployed to "
    [:a {:href "http://fathomless-savannah-40879.herokuapp.com"} "Heroku"]]
   [:p
    "Run tests with " [:code "lein test"]]
   [:p [:a {:href "/"} "Back to Todo List"]]])
