/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2025-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package fr.edf.nexus.plugins.repository.dart.internal.ui;

import javax.annotation.Priority;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.rapture.UiPluginDescriptorSupport;

@Named
@Singleton
@Priority(Integer.MAX_VALUE - 200)
public class UiPluginDescriptorImpl
    extends UiPluginDescriptorSupport
{
  public UiPluginDescriptorImpl() {
    super("nexus-repository-dart");
    setNamespace("NX.dart");
    setConfigClassName("NX.dart.app.PluginConfig");
  }
}
