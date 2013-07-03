/* This file is part of SableCC ( http://sablecc.org ).
 *
 * See the NOTICE file distributed with this work for copyright information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sablecc.sablecc.semantics;

import java.util.*;

import org.sablecc.sablecc.syntax3.node.*;

public class AlternativeTransformation {

    private Grammar grammar;

    private AAlternativeTransformation declaration;

    private AlternativeReference alternativeReference;

    private AlternativeTransformation(
            Grammar grammar,
            AAlternativeTransformation declaration) {

        this.grammar = grammar;
        this.declaration = declaration;
        this.alternativeReference = grammar
                .getAlternativeReferenceResolution(declaration
                        .getAlternativeReference());
    }

    public Token getLocation() {

        return this.alternativeReference.getLocation();
    }

    static void createDeclaredAlternativeTransformation(
            Grammar grammar,
            AAlternativeTransformation node) {

        AlternativeTransformation alternativeTransformation = new AlternativeTransformation(
                grammar, node);

        alternativeTransformation.checkElementReferences();
        alternativeTransformation.checkTypes();

        alternativeTransformation.alternativeReference.getAlternative()
                .setDeclaredTransformation(alternativeTransformation);
    }

    private void checkElementReferences() {

        // collect element references
        final List<ElementReference> elementReferences = new LinkedList<ElementReference>();
        this.declaration.apply(new TreeWalker() {

            @Override
            public void caseANaturalElementReference(
                    ANaturalElementReference node) {

                AlternativeTransformation.this.grammar
                        .resolveElementReference(node);
                elementReferences.add(AlternativeTransformation.this.grammar
                        .getElementReferenceResolution(node));
            }

            @Override
            public void caseATransformedElementReference(
                    ATransformedElementReference node) {

                AlternativeTransformation.this.grammar
                        .resolveElementReference(node);
                elementReferences.add(AlternativeTransformation.this.grammar
                        .getElementReferenceResolution(node));
            }
        });

        // check that element references are identical to elements of the
        // transformed alternative
        Iterator<ElementReference> elementReferenceIterator = elementReferences
                .iterator();
        for (Element element : this.alternativeReference.getAlternative()
                .getElements()) {

            Type type = element.getType();
            Declaration base = type.getBase();
            Declaration separator = type.getSeparator();

            if (separator != null) {
                simpleMatch(type, elementReferenceIterator);
            }
            else if (base instanceof Production) {
                ProductionTransformation productionTransformation = ((Production) base)
                        .getTransformation();
                if (productionTransformation != null) {
                    Signature signature = productionTransformation
                            .getSignature();
                    for (Type subtreeType : signature.getTypes()) {
                        if (!elementReferenceIterator.hasNext()) {
                            throw SemanticException.semanticError(
                                    "The transformation is missing a reference to : "
                                            + type + "." + subtreeType,
                                    this.alternativeReference.getLocation());
                        }
                        ElementReference elementReference = elementReferenceIterator
                                .next();
                        if (elementReference.getSubtree() == null) {
                            throw SemanticException.semanticError(
                                    "Expecting : " + type + "." + subtreeType,
                                    elementReference.getLocation());
                        }
                        if (!type.equals(this.grammar
                                .getTypeResolution(elementReference
                                        .getElementBody()))) {
                            throw SemanticException.semanticError(
                                    "Expecting : " + type + "." + subtreeType,
                                    elementReference.getLocation());
                        }
                        if (!subtreeType.equals(this.grammar
                                .getTypeResolution(elementReference
                                        .getSubtree()))) {
                            throw SemanticException.semanticError(
                                    "Expecting : " + type + "." + subtreeType,
                                    elementReference.getLocation());
                        }
                    }
                }
                else {
                    simpleMatch(type, elementReferenceIterator);
                }
            }
            else {
                simpleMatch(type, elementReferenceIterator);
            }
        }

        if (elementReferenceIterator.hasNext()) {
            ElementReference elementReference = elementReferenceIterator.next();
            throw SemanticException.semanticError(
                    "Unexpected spurious element reference.",
                    elementReference.getLocation());
        }
    }

    private void simpleMatch(
            Type type,
            Iterator<ElementReference> elementReferenceIterator) {

        if (!elementReferenceIterator.hasNext()) {
            throw SemanticException.semanticError(
                    "The transformation is missing a reference to : " + type,
                    this.alternativeReference.getLocation());
        }
        ElementReference elementReference = elementReferenceIterator.next();
        if (elementReference.getSubtree() != null) {
            throw SemanticException.semanticError("Expecting : " + type,
                    elementReference.getLocation());
        }
        if (!type.equals(this.grammar.getTypeResolution(elementReference
                .getElementBody()))) {
            throw SemanticException.semanticError("Expecting : " + type,
                    elementReference.getLocation());
        }
    }

    private void match(
            Declaration base,
            Iterator<ElementReference> elementReferenceIterator) {

    }

    private void checkTypes() {

        // TODO Auto-generated method stub

    }
}
