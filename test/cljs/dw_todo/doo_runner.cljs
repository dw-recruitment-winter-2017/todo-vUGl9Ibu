(ns dw-todo.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [dw-todo.core-test]))

(doo-tests 'dw-todo.core-test)
