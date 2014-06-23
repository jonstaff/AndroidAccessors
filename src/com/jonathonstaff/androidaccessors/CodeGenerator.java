package com.jonathonstaff.androidaccessors;
//  Created by jonstaff on 6/23/14.

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {

    private final PsiClass mClass;
    private final List<PsiField> mFields;

    public CodeGenerator(PsiClass psiClass, List<PsiField> fields) {
        mClass = psiClass;
        mFields = fields;
    }

    public void generate() {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(mClass.getProject());

        // TODO: remove old accessors

        List<PsiMethod> methods = new ArrayList<PsiMethod>();

        for (PsiField field : mFields) {
            PsiMethod getter =
                    elementFactory.createMethodFromText(generateGetterMethod(field), mClass);
            PsiMethod setter =
                    elementFactory.createMethodFromText(generateSetterMethod(field), mClass);
            methods.add(getter);
            methods.add(setter);
        }

        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mClass.getProject());

        for (PsiMethod method : methods) {
            styleManager.shortenClassReferences(mClass.add(method));
        }
    }

    private static String generateGetterMethod(PsiField field) {
        StringBuilder sb = new StringBuilder("public ");
        sb.append(field.getType().getPresentableText());

        StringBuilder sb2 = new StringBuilder(field.getName());

        // verify that the first character is an 'm' and the second is uppercase
        if (sb2.charAt(0) == 'm' && sb2.charAt(1) < 97) {
            sb2.deleteCharAt(0);
        }
        sb2.setCharAt(0, Character.toUpperCase(sb2.charAt(0)));

        sb.append(" get").append(sb2.toString()).append("() { return ").append(field.getName())
          .append("; }");
        return sb.toString();
    }

    private static String generateSetterMethod(PsiField field) {
        StringBuilder sb = new StringBuilder("public void set");
        StringBuilder sb2 = new StringBuilder(field.getName());
        boolean needsThis = true;

        // verify that the first character is an 'm' and the second is uppercase
        if (sb2.charAt(0) == 'm' && sb2.charAt(1) < 97) {
            sb2.deleteCharAt(0);
            needsThis = false;
        }
        sb2.setCharAt(0, Character.toUpperCase(sb2.charAt(0)));
        String paramUpper = sb2.toString();

        sb2.setCharAt(0, Character.toLowerCase(sb2.charAt(0)));
        String param = sb2.toString();

        sb.append(paramUpper).append("(").append(field.getType().getPresentableText()).append(" ")
          .append(param).append(") { ");

        if (needsThis) {
            sb.append("this.");
        }
        sb.append(field.getName()).append(" = ").append(param).append("; }");

        return sb.toString();
    }
}
