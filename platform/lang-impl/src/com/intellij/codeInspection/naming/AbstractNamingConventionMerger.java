// Copyright 2000-2017 JetBrains s.r.o.
// Use of this source code is governed by the Apache 2.0 license that can be
// found in the LICENSE file.
package com.intellij.codeInspection.naming;

import com.intellij.codeInspection.ex.InspectionElementsMergerBase;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ObjectUtils;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class AbstractNamingConventionMerger<T extends PsiNameIdentifierOwner> extends InspectionElementsMergerBase {
  private AbstractNamingConventionInspection<T> myNewInspection;

  public AbstractNamingConventionMerger(AbstractNamingConventionInspection<T> inspection) {
    myNewInspection = inspection;
  }

  @NotNull
  @Override
  public String getMergedToolName() {
    return myNewInspection.getShortName();
  }

  @NotNull
  @Override
  public String[] getSourceToolNames() {
    return ArrayUtil.toStringArray(myNewInspection.getOldToolNames());
  }

  @Override
  protected boolean areSettingsMerged(Map<String, Element> inspectionsSettings, Element inspectionElement) {
    final Element merge = merge(inspectionsSettings, false);
    if (merge != null) {
      myNewInspection.readSettings(merge);
      merge.removeContent();
      myNewInspection.writeSettings(merge);
      return JDOMUtil.areElementsEqual(merge, inspectionElement);
    }
    return false;
  }

  @Override
  protected Element wrapElement(String sourceToolName, Element sourceElement, Element toolElement) {
    Element element = new Element("extension").setAttribute("name", sourceToolName);
    if (sourceElement != null) {
      element.setAttribute("enabled", ObjectUtils.notNull(sourceElement.getAttributeValue("enabled"), "false"));
    }
    toolElement.addContent(element);
    return element;
  }
}
