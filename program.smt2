(set-option :smt.string_solver z3str3)


(declare-fun |kernel::__CPAchecker_TMP_1@3| () (_ BitVec 32))
(declare-fun |kernel::input@2| () String)
(declare-fun |kernel::diff@16| () (_ BitVec 32))
(declare-fun |kernel::tmp@15| () String)
(declare-fun |kernel::offset@15| () (_ BitVec 32))
(declare-fun |kernel::offset@16| () (_ BitVec 32))
(declare-fun |kernel::diff@15| () (_ BitVec 32))
(declare-fun |kernel::tmp@14| () String)
(declare-fun |kernel::offset@14| () (_ BitVec 32))
(declare-fun |kernel::diff@14| () (_ BitVec 32))
(declare-fun |kernel::tmp@13| () String)
(declare-fun |kernel::offset@13| () (_ BitVec 32))
(declare-fun |kernel::diff@13| () (_ BitVec 32))
(declare-fun |kernel::tmp@12| () String)
(declare-fun |kernel::offset@12| () (_ BitVec 32))
(declare-fun |kernel::diff@12| () (_ BitVec 32))
(declare-fun |kernel::tmp@11| () String)
(declare-fun |kernel::offset@11| () (_ BitVec 32))
(declare-fun |kernel::diff@11| () (_ BitVec 32))
(declare-fun |kernel::tmp@10| () String)
(declare-fun |kernel::offset@10| () (_ BitVec 32))
(declare-fun |kernel::diff@10| () (_ BitVec 32))
(declare-fun |kernel::tmp@9| () String)
(declare-fun |kernel::i@10| () (_ BitVec 32))
(declare-fun |kernel::i@9| () (_ BitVec 32))
(declare-fun |kernel::diff@9| () (_ BitVec 32))
(declare-fun |kernel::comp@2| () String)
(declare-fun |kernel::offset@9| () (_ BitVec 32))
(declare-fun |kernel::tmp@8| () String)
(declare-fun |kernel::i@8| () (_ BitVec 32))
(declare-fun |kernel::diff@8| () (_ BitVec 32))
(declare-fun |kernel::offset@8| () (_ BitVec 32))
(declare-fun |kernel::tmp@7| () String)
(declare-fun |kernel::i@7| () (_ BitVec 32))
(declare-fun |kernel::diff@7| () (_ BitVec 32))
(declare-fun |kernel::offset@7| () (_ BitVec 32))
(declare-fun |kernel::tmp@6| () String)
(declare-fun |kernel::i@6| () (_ BitVec 32))
(declare-fun |kernel::diff@6| () (_ BitVec 32))
(declare-fun |kernel::offset@6| () (_ BitVec 32))
(declare-fun |kernel::tmp@5| () String)
(declare-fun |kernel::i@5| () (_ BitVec 32))
(declare-fun |kernel::diff@5| () (_ BitVec 32))
(declare-fun |kernel::offset@5| () (_ BitVec 32))
(declare-fun |kernel::tmp@4| () String)
(declare-fun |kernel::i@4| () (_ BitVec 32))
(declare-fun |kernel::diff@4| () (_ BitVec 32))
(declare-fun |kernel::offset@4| () (_ BitVec 32))
(declare-fun |kernel::tmp@3| () String)
(declare-fun |kernel::i@3| () (_ BitVec 32))
(declare-fun |kernel::diff@3| () (_ BitVec 32))
(declare-fun |kernel::offset@3| () (_ BitVec 32))
(declare-fun |kernel::__CPAchecker_TMP_0@3| () (_ BitVec 32))
(assert (let ((a!1 (re.++ (re.+ (re.union (str.to.re "a") (str.to.re "b")))
                  (str.to.re "\x00")))
      (a!3 (not (= (str.at |kernel::comp@2| (bv2int |kernel::i@3|))
                   |kernel::tmp@3|)))
      (a!5 (re.++ (str.to.re "a")
                  (re.union (str.to.re "")
                            (re.union (str.to.re "b") (str.to.re "a")))))
      (a!10 (not (= (str.at |kernel::comp@2| (bv2int |kernel::i@4|))
                    |kernel::tmp@4|)))
      (a!14 (not (= (str.at |kernel::comp@2| (bv2int |kernel::i@5|))
                    |kernel::tmp@5|)))
      (a!18 (not (= (str.at |kernel::comp@2| (bv2int |kernel::i@6|))
                    |kernel::tmp@6|)))
      (a!22 (not (= (str.at |kernel::comp@2| (bv2int |kernel::i@7|))
                    |kernel::tmp@7|)))
      (a!26 (not (= (str.at |kernel::comp@2| (bv2int |kernel::i@8|))
                    |kernel::tmp@8|)))
      (a!30 (not (= (str.at |kernel::comp@2| (bv2int |kernel::i@9|))
                    |kernel::tmp@9|))))
(let ((a!2 (and (= |kernel::__CPAchecker_TMP_0@3|
                   (ite (str.in.re |kernel::input@2| a!1) #x00000001 #x00000000))
                (not (= |kernel::__CPAchecker_TMP_0@3| #x00000000))
                (= |kernel::i@3| #x00000000)
                (= |kernel::offset@3| #x00000000)
                (= (str.at |kernel::comp@2| (bv2int #x00000000)) "a")
                (= (str.at |kernel::comp@2| (bv2int #x00000001)) "b")
                (= (str.at |kernel::comp@2| (bv2int #x00000002)) "a")
                (= (str.at |kernel::comp@2| (bv2int #x00000003)) "b")
                (= (str.at |kernel::comp@2| (bv2int #x00000004)) "a")
                (= (str.at |kernel::comp@2| (bv2int #x00000005)) "b")
                (= (str.at |kernel::comp@2| (bv2int #x00000006)) "\x00")
                (= |kernel::diff@3| #x00000000)
                (= |kernel::tmp@3|
                   (str.at |kernel::input@2| (bv2int |kernel::offset@3|)))
                (= |kernel::offset@4| (bvadd |kernel::offset@3| #x00000001))))
      (a!6 (re.++ (str.to.re "b")
                  (re.++ (str.to.re "a") (re.++ (str.to.re "b") a!5)))))
(let ((a!4 (or (and a!2
                    a!3
                    (= |kernel::diff@4| (bvadd |kernel::diff@3| #x00000001)))
               (and a!2 (not a!3) (= |kernel::diff@4| |kernel::diff@3|))))
      (a!7 (str.in.re |kernel::input@2|
                      (re.++ (re.++ (str.to.re "a") a!6)
                             (re.+ (str.to.re "\x00"))))))
(let ((a!8 (and (and a!4 (= |kernel::i@4| (bvadd |kernel::i@3| #x00000001)))
                (not (not (= |kernel::tmp@3| "\x00")))
                (bvslt |kernel::i@4| #x00000007)
                (= |kernel::diff@5|
                   (bvadd |kernel::diff@4| (bvsub #x00000007 |kernel::i@4|)))
                (not (= |kernel::tmp@3| "\x00"))
                (= |kernel::diff@6| (bvadd |kernel::diff@5| #x00000001))
                (= |kernel::tmp@4|
                   (str.at |kernel::input@2| (bv2int |kernel::offset@4|)))
                (= |kernel::offset@5| (bvadd |kernel::offset@4| #x00000001))
                (not (= |kernel::tmp@4| "\x00"))
                (= |kernel::diff@7| (bvadd |kernel::diff@6| #x00000001))
                (= |kernel::tmp@5|
                   (str.at |kernel::input@2| (bv2int |kernel::offset@5|)))
                (= |kernel::offset@6| (bvadd |kernel::offset@5| #x00000001))
                (not (= |kernel::tmp@5| "\x00"))
                (= |kernel::diff@8| (bvadd |kernel::diff@7| #x00000001))
                (= |kernel::tmp@6|
                   (str.at |kernel::input@2| (bv2int |kernel::offset@6|)))
                (= |kernel::offset@7| (bvadd |kernel::offset@6| #x00000001))
                (not (= |kernel::tmp@6| "\x00"))
                (= |kernel::diff@9| (bvadd |kernel::diff@8| #x00000001))
                (= |kernel::tmp@7|
                   (str.at |kernel::input@2| (bv2int |kernel::offset@7|)))
                (= |kernel::offset@8| (bvadd |kernel::offset@7| #x00000001))
                (not (= |kernel::tmp@7| "\x00"))
                (= |kernel::diff@10| (bvadd |kernel::diff@9| #x00000001))
                (= |kernel::tmp@8|
                   (str.at |kernel::input@2| (bv2int |kernel::offset@8|)))
                (= |kernel::offset@9| (bvadd |kernel::offset@8| #x00000001))
                (not (= |kernel::tmp@8| "\x00"))
                (= |kernel::diff@11| (bvadd |kernel::diff@10| #x00000001))
                (= |kernel::tmp@9|
                   (str.at |kernel::input@2| (bv2int |kernel::offset@9|)))
                (= |kernel::offset@10| (bvadd |kernel::offset@9| #x00000001))
                (not (not (= |kernel::tmp@9| "\x00")))
                (bvsle |kernel::diff@11| #x00000002)
                (= |kernel::__CPAchecker_TMP_1@3|
                   (ite a!7 #x00000001 #x00000000))
                (= #x00000000 |kernel::__CPAchecker_TMP_1@3|)))
      (a!9 (and (and a!4 (= |kernel::i@4| (bvadd |kernel::i@3| #x00000001)))
                (not (= |kernel::tmp@3| "\x00"))
                (bvslt |kernel::i@4| #x00000007)
                (= |kernel::tmp@4|
                   (str.at |kernel::input@2| (bv2int |kernel::offset@4|)))
                (= |kernel::offset@5| (bvadd |kernel::offset@4| #x00000001)))))
(let ((a!11 (or (and a!9
                     a!10
                     (= |kernel::diff@5| (bvadd |kernel::diff@4| #x00000001)))
                (and a!9 (not a!10) (= |kernel::diff@5| |kernel::diff@4|)))))
(let ((a!12 (and (and a!11 (= |kernel::i@5| (bvadd |kernel::i@4| #x00000001)))
                 (not (not (= |kernel::tmp@4| "\x00")))
                 (bvslt |kernel::i@5| #x00000007)
                 (= |kernel::diff@6|
                    (bvadd |kernel::diff@5| (bvsub #x00000007 |kernel::i@5|)))
                 (not (= |kernel::tmp@4| "\x00"))
                 (= |kernel::diff@7| (bvadd |kernel::diff@6| #x00000001))
                 (= |kernel::tmp@5|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@5|)))
                 (= |kernel::offset@6| (bvadd |kernel::offset@5| #x00000001))
                 (not (= |kernel::tmp@5| "\x00"))
                 (= |kernel::diff@8| (bvadd |kernel::diff@7| #x00000001))
                 (= |kernel::tmp@6|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@6|)))
                 (= |kernel::offset@7| (bvadd |kernel::offset@6| #x00000001))
                 (not (= |kernel::tmp@6| "\x00"))
                 (= |kernel::diff@9| (bvadd |kernel::diff@8| #x00000001))
                 (= |kernel::tmp@7|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@7|)))
                 (= |kernel::offset@8| (bvadd |kernel::offset@7| #x00000001))
                 (not (= |kernel::tmp@7| "\x00"))
                 (= |kernel::diff@10| (bvadd |kernel::diff@9| #x00000001))
                 (= |kernel::tmp@8|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@8|)))
                 (= |kernel::offset@9| (bvadd |kernel::offset@8| #x00000001))
                 (not (= |kernel::tmp@8| "\x00"))
                 (= |kernel::diff@11| (bvadd |kernel::diff@10| #x00000001))
                 (= |kernel::tmp@9|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@9|)))
                 (= |kernel::offset@10| (bvadd |kernel::offset@9| #x00000001))
                 (not (= |kernel::tmp@9| "\x00"))
                 (= |kernel::diff@12| (bvadd |kernel::diff@11| #x00000001))
                 (= |kernel::tmp@10|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@10|)))
                 (= |kernel::offset@11| (bvadd |kernel::offset@10| #x00000001))
                 (not (not (= |kernel::tmp@10| "\x00")))
                 (bvsle |kernel::diff@12| #x00000002)
                 (= |kernel::__CPAchecker_TMP_1@3|
                    (ite a!7 #x00000001 #x00000000))
                 (= #x00000000 |kernel::__CPAchecker_TMP_1@3|)))
      (a!13 (and (and a!11 (= |kernel::i@5| (bvadd |kernel::i@4| #x00000001)))
                 (not (= |kernel::tmp@4| "\x00"))
                 (bvslt |kernel::i@5| #x00000007)
                 (= |kernel::tmp@5|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@5|)))
                 (= |kernel::offset@6| (bvadd |kernel::offset@5| #x00000001)))))
(let ((a!15 (or (and a!13
                     a!14
                     (= |kernel::diff@6| (bvadd |kernel::diff@5| #x00000001)))
                (and a!13 (not a!14) (= |kernel::diff@6| |kernel::diff@5|)))))
(let ((a!16 (and (and a!15 (= |kernel::i@6| (bvadd |kernel::i@5| #x00000001)))
                 (not (not (= |kernel::tmp@5| "\x00")))
                 (bvslt |kernel::i@6| #x00000007)
                 (= |kernel::diff@7|
                    (bvadd |kernel::diff@6| (bvsub #x00000007 |kernel::i@6|)))
                 (not (= |kernel::tmp@5| "\x00"))
                 (= |kernel::diff@8| (bvadd |kernel::diff@7| #x00000001))
                 (= |kernel::tmp@6|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@6|)))
                 (= |kernel::offset@7| (bvadd |kernel::offset@6| #x00000001))
                 (not (= |kernel::tmp@6| "\x00"))
                 (= |kernel::diff@9| (bvadd |kernel::diff@8| #x00000001))
                 (= |kernel::tmp@7|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@7|)))
                 (= |kernel::offset@8| (bvadd |kernel::offset@7| #x00000001))
                 (not (= |kernel::tmp@7| "\x00"))
                 (= |kernel::diff@10| (bvadd |kernel::diff@9| #x00000001))
                 (= |kernel::tmp@8|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@8|)))
                 (= |kernel::offset@9| (bvadd |kernel::offset@8| #x00000001))
                 (not (= |kernel::tmp@8| "\x00"))
                 (= |kernel::diff@11| (bvadd |kernel::diff@10| #x00000001))
                 (= |kernel::tmp@9|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@9|)))
                 (= |kernel::offset@10| (bvadd |kernel::offset@9| #x00000001))
                 (not (= |kernel::tmp@9| "\x00"))
                 (= |kernel::diff@12| (bvadd |kernel::diff@11| #x00000001))
                 (= |kernel::tmp@10|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@10|)))
                 (= |kernel::offset@11| (bvadd |kernel::offset@10| #x00000001))
                 (not (= |kernel::tmp@10| "\x00"))
                 (= |kernel::diff@13| (bvadd |kernel::diff@12| #x00000001))
                 (= |kernel::tmp@11|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@11|)))
                 (= |kernel::offset@12| (bvadd |kernel::offset@11| #x00000001))
                 (not (not (= |kernel::tmp@11| "\x00")))
                 (bvsle |kernel::diff@13| #x00000002)
                 (= |kernel::__CPAchecker_TMP_1@3|
                    (ite a!7 #x00000001 #x00000000))
                 (= #x00000000 |kernel::__CPAchecker_TMP_1@3|)))
      (a!17 (and (and a!15 (= |kernel::i@6| (bvadd |kernel::i@5| #x00000001)))
                 (not (= |kernel::tmp@5| "\x00"))
                 (bvslt |kernel::i@6| #x00000007)
                 (= |kernel::tmp@6|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@6|)))
                 (= |kernel::offset@7| (bvadd |kernel::offset@6| #x00000001)))))
(let ((a!19 (or (and a!17
                     a!18
                     (= |kernel::diff@7| (bvadd |kernel::diff@6| #x00000001)))
                (and a!17 (not a!18) (= |kernel::diff@7| |kernel::diff@6|)))))
(let ((a!20 (and (and a!19 (= |kernel::i@7| (bvadd |kernel::i@6| #x00000001)))
                 (not (not (= |kernel::tmp@6| "\x00")))
                 (bvslt |kernel::i@7| #x00000007)
                 (= |kernel::diff@8|
                    (bvadd |kernel::diff@7| (bvsub #x00000007 |kernel::i@7|)))
                 (not (= |kernel::tmp@6| "\x00"))
                 (= |kernel::diff@9| (bvadd |kernel::diff@8| #x00000001))
                 (= |kernel::tmp@7|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@7|)))
                 (= |kernel::offset@8| (bvadd |kernel::offset@7| #x00000001))
                 (not (= |kernel::tmp@7| "\x00"))
                 (= |kernel::diff@10| (bvadd |kernel::diff@9| #x00000001))
                 (= |kernel::tmp@8|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@8|)))
                 (= |kernel::offset@9| (bvadd |kernel::offset@8| #x00000001))
                 (not (= |kernel::tmp@8| "\x00"))
                 (= |kernel::diff@11| (bvadd |kernel::diff@10| #x00000001))
                 (= |kernel::tmp@9|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@9|)))
                 (= |kernel::offset@10| (bvadd |kernel::offset@9| #x00000001))
                 (not (= |kernel::tmp@9| "\x00"))
                 (= |kernel::diff@12| (bvadd |kernel::diff@11| #x00000001))
                 (= |kernel::tmp@10|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@10|)))
                 (= |kernel::offset@11| (bvadd |kernel::offset@10| #x00000001))
                 (not (= |kernel::tmp@10| "\x00"))
                 (= |kernel::diff@13| (bvadd |kernel::diff@12| #x00000001))
                 (= |kernel::tmp@11|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@11|)))
                 (= |kernel::offset@12| (bvadd |kernel::offset@11| #x00000001))
                 (not (= |kernel::tmp@11| "\x00"))
                 (= |kernel::diff@14| (bvadd |kernel::diff@13| #x00000001))
                 (= |kernel::tmp@12|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@12|)))
                 (= |kernel::offset@13| (bvadd |kernel::offset@12| #x00000001))
                 (not (not (= |kernel::tmp@12| "\x00")))
                 (bvsle |kernel::diff@14| #x00000002)
                 (= |kernel::__CPAchecker_TMP_1@3|
                    (ite a!7 #x00000001 #x00000000))
                 (= #x00000000 |kernel::__CPAchecker_TMP_1@3|)))
      (a!21 (and (and a!19 (= |kernel::i@7| (bvadd |kernel::i@6| #x00000001)))
                 (not (= |kernel::tmp@6| "\x00"))
                 (bvslt |kernel::i@7| #x00000007)
                 (= |kernel::tmp@7|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@7|)))
                 (= |kernel::offset@8| (bvadd |kernel::offset@7| #x00000001)))))
(let ((a!23 (or (and a!21
                     a!22
                     (= |kernel::diff@8| (bvadd |kernel::diff@7| #x00000001)))
                (and a!21 (not a!22) (= |kernel::diff@8| |kernel::diff@7|)))))
(let ((a!24 (and (and a!23 (= |kernel::i@8| (bvadd |kernel::i@7| #x00000001)))
                 (not (not (= |kernel::tmp@7| "\x00")))
                 (bvslt |kernel::i@8| #x00000007)
                 (= |kernel::diff@9|
                    (bvadd |kernel::diff@8| (bvsub #x00000007 |kernel::i@8|)))
                 (not (= |kernel::tmp@7| "\x00"))
                 (= |kernel::diff@10| (bvadd |kernel::diff@9| #x00000001))
                 (= |kernel::tmp@8|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@8|)))
                 (= |kernel::offset@9| (bvadd |kernel::offset@8| #x00000001))
                 (not (= |kernel::tmp@8| "\x00"))
                 (= |kernel::diff@11| (bvadd |kernel::diff@10| #x00000001))
                 (= |kernel::tmp@9|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@9|)))
                 (= |kernel::offset@10| (bvadd |kernel::offset@9| #x00000001))
                 (not (= |kernel::tmp@9| "\x00"))
                 (= |kernel::diff@12| (bvadd |kernel::diff@11| #x00000001))
                 (= |kernel::tmp@10|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@10|)))
                 (= |kernel::offset@11| (bvadd |kernel::offset@10| #x00000001))
                 (not (= |kernel::tmp@10| "\x00"))
                 (= |kernel::diff@13| (bvadd |kernel::diff@12| #x00000001))
                 (= |kernel::tmp@11|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@11|)))
                 (= |kernel::offset@12| (bvadd |kernel::offset@11| #x00000001))
                 (not (= |kernel::tmp@11| "\x00"))
                 (= |kernel::diff@14| (bvadd |kernel::diff@13| #x00000001))
                 (= |kernel::tmp@12|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@12|)))
                 (= |kernel::offset@13| (bvadd |kernel::offset@12| #x00000001))
                 (not (= |kernel::tmp@12| "\x00"))
                 (= |kernel::diff@15| (bvadd |kernel::diff@14| #x00000001))
                 (= |kernel::tmp@13|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@13|)))
                 (= |kernel::offset@14| (bvadd |kernel::offset@13| #x00000001))
                 (not (not (= |kernel::tmp@13| "\x00")))
                 (bvsle |kernel::diff@15| #x00000002)
                 (= |kernel::__CPAchecker_TMP_1@3|
                    (ite a!7 #x00000001 #x00000000))
                 (= #x00000000 |kernel::__CPAchecker_TMP_1@3|)))
      (a!25 (and (and a!23 (= |kernel::i@8| (bvadd |kernel::i@7| #x00000001)))
                 (not (= |kernel::tmp@7| "\x00"))
                 (bvslt |kernel::i@8| #x00000007)
                 (= |kernel::tmp@8|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@8|)))
                 (= |kernel::offset@9| (bvadd |kernel::offset@8| #x00000001)))))
(let ((a!27 (or (and a!25
                     a!26
                     (= |kernel::diff@9| (bvadd |kernel::diff@8| #x00000001)))
                (and a!25 (not a!26) (= |kernel::diff@9| |kernel::diff@8|)))))
(let ((a!28 (and (and a!27 (= |kernel::i@9| (bvadd |kernel::i@8| #x00000001)))
                 (not (not (= |kernel::tmp@8| "\x00")))
                 (bvslt |kernel::i@9| #x00000007)
                 (= |kernel::diff@10|
                    (bvadd |kernel::diff@9| (bvsub #x00000007 |kernel::i@9|)))
                 (not (= |kernel::tmp@8| "\x00"))
                 (= |kernel::diff@11| (bvadd |kernel::diff@10| #x00000001))
                 (= |kernel::tmp@9|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@9|)))
                 (= |kernel::offset@10| (bvadd |kernel::offset@9| #x00000001))
                 (not (= |kernel::tmp@9| "\x00"))
                 (= |kernel::diff@12| (bvadd |kernel::diff@11| #x00000001))
                 (= |kernel::tmp@10|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@10|)))
                 (= |kernel::offset@11| (bvadd |kernel::offset@10| #x00000001))
                 (not (= |kernel::tmp@10| "\x00"))
                 (= |kernel::diff@13| (bvadd |kernel::diff@12| #x00000001))
                 (= |kernel::tmp@11|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@11|)))
                 (= |kernel::offset@12| (bvadd |kernel::offset@11| #x00000001))
                 (not (= |kernel::tmp@11| "\x00"))
                 (= |kernel::diff@14| (bvadd |kernel::diff@13| #x00000001))
                 (= |kernel::tmp@12|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@12|)))
                 (= |kernel::offset@13| (bvadd |kernel::offset@12| #x00000001))
                 (not (= |kernel::tmp@12| "\x00"))
                 (= |kernel::diff@15| (bvadd |kernel::diff@14| #x00000001))
                 (= |kernel::tmp@13|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@13|)))
                 (= |kernel::offset@14| (bvadd |kernel::offset@13| #x00000001))
                 (not (= |kernel::tmp@13| "\x00"))
                 (= |kernel::diff@16| (bvadd |kernel::diff@15| #x00000001))
                 (= |kernel::tmp@14|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@14|)))
                 (= |kernel::offset@15| (bvadd |kernel::offset@14| #x00000001))
                 (not (not (= |kernel::tmp@14| "\x00")))
                 (bvsle |kernel::diff@16| #x00000002)
                 (= |kernel::__CPAchecker_TMP_1@3|
                    (ite a!7 #x00000001 #x00000000))
                 (= #x00000000 |kernel::__CPAchecker_TMP_1@3|)))
      (a!29 (and (and a!27 (= |kernel::i@9| (bvadd |kernel::i@8| #x00000001)))
                 (not (= |kernel::tmp@8| "\x00"))
                 (bvslt |kernel::i@9| #x00000007)
                 (= |kernel::tmp@9|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@9|)))
                 (= |kernel::offset@10| (bvadd |kernel::offset@9| #x00000001)))))
(let ((a!31 (or (and a!29
                     a!30
                     (= |kernel::diff@10| (bvadd |kernel::diff@9| #x00000001)))
                (and a!29 (not a!30) (= |kernel::diff@10| |kernel::diff@9|)))))
(let ((a!32 (and (and a!31 (= |kernel::i@10| (bvadd |kernel::i@9| #x00000001)))
                 (not (= |kernel::tmp@9| "\x00"))
                 (not (bvslt |kernel::i@10| #x00000007))))
      (a!33 (and (and a!31 (= |kernel::i@10| (bvadd |kernel::i@9| #x00000001)))
                 (not (not (= |kernel::tmp@9| "\x00"))))))
(let ((a!34 (and (and (or a!32 a!33) (not (bvslt |kernel::i@10| #x00000007)))
                 (not (not (= |kernel::tmp@9| "\x00")))
                 (bvsle |kernel::diff@10| #x00000002)
                 (= |kernel::__CPAchecker_TMP_1@3|
                    (ite a!7 #x00000001 #x00000000))
                 (= #x00000000 |kernel::__CPAchecker_TMP_1@3|)))
      (a!35 (and (and (or a!32 a!33) (not (bvslt |kernel::i@10| #x00000007)))
                 (not (= |kernel::tmp@9| "\x00"))
                 (= |kernel::diff@11| (bvadd |kernel::diff@10| #x00000001))
                 (= |kernel::tmp@10|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@10|)))
                 (= |kernel::offset@11| (bvadd |kernel::offset@10| #x00000001)))))
(let ((a!36 (and a!35
                 (not (not (= |kernel::tmp@10| "\x00")))
                 (bvsle |kernel::diff@11| #x00000002)
                 (= |kernel::__CPAchecker_TMP_1@3|
                    (ite a!7 #x00000001 #x00000000))
                 (= #x00000000 |kernel::__CPAchecker_TMP_1@3|)))
      (a!37 (and a!35
                 (not (= |kernel::tmp@10| "\x00"))
                 (= |kernel::diff@12| (bvadd |kernel::diff@11| #x00000001))
                 (= |kernel::tmp@11|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@11|)))
                 (= |kernel::offset@12| (bvadd |kernel::offset@11| #x00000001)))))
(let ((a!38 (and a!37
                 (not (not (= |kernel::tmp@11| "\x00")))
                 (bvsle |kernel::diff@12| #x00000002)
                 (= |kernel::__CPAchecker_TMP_1@3|
                    (ite a!7 #x00000001 #x00000000))
                 (= #x00000000 |kernel::__CPAchecker_TMP_1@3|)))
      (a!39 (and a!37
                 (not (= |kernel::tmp@11| "\x00"))
                 (= |kernel::diff@13| (bvadd |kernel::diff@12| #x00000001))
                 (= |kernel::tmp@12|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@12|)))
                 (= |kernel::offset@13| (bvadd |kernel::offset@12| #x00000001)))))
(let ((a!40 (and a!39
                 (not (not (= |kernel::tmp@12| "\x00")))
                 (bvsle |kernel::diff@13| #x00000002)
                 (= |kernel::__CPAchecker_TMP_1@3|
                    (ite a!7 #x00000001 #x00000000))
                 (= #x00000000 |kernel::__CPAchecker_TMP_1@3|)))
      (a!41 (and a!39
                 (not (= |kernel::tmp@12| "\x00"))
                 (= |kernel::diff@14| (bvadd |kernel::diff@13| #x00000001))
                 (= |kernel::tmp@13|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@13|)))
                 (= |kernel::offset@14| (bvadd |kernel::offset@13| #x00000001)))))
(let ((a!42 (and a!41
                 (not (not (= |kernel::tmp@13| "\x00")))
                 (bvsle |kernel::diff@14| #x00000002)
                 (= |kernel::__CPAchecker_TMP_1@3|
                    (ite a!7 #x00000001 #x00000000))
                 (= #x00000000 |kernel::__CPAchecker_TMP_1@3|)))
      (a!43 (and a!41
                 (not (= |kernel::tmp@13| "\x00"))
                 (= |kernel::diff@15| (bvadd |kernel::diff@14| #x00000001))
                 (= |kernel::tmp@14|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@14|)))
                 (= |kernel::offset@15| (bvadd |kernel::offset@14| #x00000001)))))
(let ((a!44 (and a!43
                 (not (not (= |kernel::tmp@14| "\x00")))
                 (bvsle |kernel::diff@15| #x00000002)
                 (= |kernel::__CPAchecker_TMP_1@3|
                    (ite a!7 #x00000001 #x00000000))
                 (= #x00000000 |kernel::__CPAchecker_TMP_1@3|)))
      (a!45 (and a!43
                 (not (= |kernel::tmp@14| "\x00"))
                 (= |kernel::diff@16| (bvadd |kernel::diff@15| #x00000001))
                 (= |kernel::tmp@15|
                    (str.at |kernel::input@2| (bv2int |kernel::offset@15|)))
                 (= |kernel::offset@16| (bvadd |kernel::offset@15| #x00000001))
                 (not (not (= |kernel::tmp@15| "\x00")))
                 (bvsle |kernel::diff@16| #x00000002)
                 (= |kernel::__CPAchecker_TMP_1@3|
                    (ite a!7 #x00000001 #x00000000))
                 (= #x00000000 |kernel::__CPAchecker_TMP_1@3|))))
  (not (not (or a!8 a!12 a!16 a!20 a!24 a!28 a!34 a!36 a!38 a!40 a!42 a!44 a!45))))))))))))))))))))))))))
  
(check-sat)
(get-model)