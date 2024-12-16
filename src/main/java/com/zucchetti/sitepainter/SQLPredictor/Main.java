package com.zucchetti.sitepainter.SQLPredictor;
//package SQLPredictor;


public class Main {
    public static void main(String[] args) {
        String predictorWithFields = "<moto>(    t.aaa+77, t.tttt, t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13), t.bbb, tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz  )";
        //String predictorWithFields = "<       good   >(      qqq555eee + left(t.nome1_1,11) + left(t.nome1_2,12) )";
        MLSQLExpander expander = new MLSQLExpander();
        String query = expander.translate(predictorWithFields);
        System.out.println(query);
    }
}