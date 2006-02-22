/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids <sdavids@gmx.de> - Images for menu items (27481)
 *******************************************************************************/
package org.eclipse.ui.internal.navigator.resources.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.DeleteResourceAction;
import org.eclipse.ui.actions.MoveResourceAction;
import org.eclipse.ui.actions.RenameResourceAction;
import org.eclipse.ui.navigator.ICommonMenuConstants;

/**
 * This is the action group for refactor actions, including global action
 * handlers for copy, paste and delete.
 * 
 * @since 2.0
 */
public class RefactorActionGroup extends ActionGroup {

	private Clipboard clipboard;

	private CopyAction copyAction;

	private DeleteResourceAction deleteAction;

	private PasteAction pasteAction;

	private RenameResourceAction renameAction;

	private MoveResourceAction moveAction;

	private TextActionHandler textActionHandler;

	private Shell shell;

	/**
	 * 
	 * @param aProvider
	 * @param aShell
	 */
	public RefactorActionGroup(Shell aShell) {
		shell = aShell;
		makeActions();
	}

	public void dispose() {
		if (clipboard != null) {
			clipboard.dispose();
			clipboard = null;
		}
		super.dispose();
	}

	public void fillContextMenu(IMenuManager menu) {
		IStructuredSelection selection = (IStructuredSelection) getContext()
				.getSelection();

		boolean anyResourceSelected = !selection.isEmpty()
				&& ResourceSelectionUtil.allResourcesAreOfType(selection,
						IResource.PROJECT | IResource.FOLDER | IResource.FILE);

		copyAction.selectionChanged(selection);
		menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, copyAction);
		pasteAction.selectionChanged(selection);
		menu.insertAfter(copyAction.getId(), pasteAction);

		if (anyResourceSelected) {
			deleteAction.selectionChanged(selection);
			menu.insertAfter(pasteAction.getId(), deleteAction);
			moveAction.selectionChanged(selection);
			menu.insertAfter(deleteAction.getId(), moveAction);
			renameAction.selectionChanged(selection);
			menu.insertAfter(moveAction.getId(), renameAction);
		}
	}

	public void fillActionBars(IActionBars actionBars) {

		if (textActionHandler == null) {
			textActionHandler = new TextActionHandler(actionBars); // hooks
																	// handlers
			textActionHandler.setCopyAction(copyAction);
			textActionHandler.setPasteAction(pasteAction);
			textActionHandler.setDeleteAction(deleteAction);
			renameAction.setTextActionHandler(textActionHandler);
		}
		updateActionBars();

		textActionHandler.updateActionBars();

		actionBars.setGlobalActionHandler(ActionFactory.MOVE.getId(),
				moveAction);
		actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(),
				renameAction);
	}

	/**
	 * Handles a key pressed event by invoking the appropriate action.
	 * 
	 * @param event
	 *            The Key Event
	 */
	public void handleKeyPressed(KeyEvent event) {
		if (event.character == SWT.DEL && event.stateMask == 0) {
			if (deleteAction.isEnabled()) {
				deleteAction.run();
			}

			// Swallow the event.
			event.doit = false;

		} else if (event.keyCode == SWT.F2 && event.stateMask == 0) {
			if (renameAction.isEnabled()) {
				renameAction.run();
			}

			// Swallow the event.
			event.doit = false;
		}
	}

	protected void makeActions() {
		clipboard = new Clipboard(shell.getDisplay());

		pasteAction = new PasteAction(shell, clipboard);
		ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
		pasteAction.setDisabledImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
		pasteAction.setImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));

		copyAction = new CopyAction(shell, clipboard, pasteAction);
		copyAction.setDisabledImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		copyAction.setImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

		moveAction = new MoveResourceAction(shell);
		renameAction = new RenameResourceAction(shell);

		deleteAction = new DeleteResourceAction(shell);
		deleteAction.setDisabledImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		deleteAction.setImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	}

	public void updateActionBars() {
		IStructuredSelection selection = (IStructuredSelection) getContext()
				.getSelection();

		copyAction.selectionChanged(selection);
		pasteAction.selectionChanged(selection);
		deleteAction.selectionChanged(selection);
		moveAction.selectionChanged(selection);
		renameAction.selectionChanged(selection);
	}

}
