/*
 * This file is part of Modelica Development Tooling.
 *
 * Copyright (c) 2005, Linköpings universitet, Department of
 * Computer and Information Science, PELAB
 *
 * All rights reserved.
 *
 * (The new BSD license, see also
 * http://www.opensource.org/licenses/bsd-license.php)
 *
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * * Neither the name of Linköpings universitet nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.modelica.mdt.core.compiler;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.modelica.mdt.internal.core.CorePlugin;

/**
 * This exception is thrown when there was error instantiating the
 * compiler object. Use getProblemType() to get details of the
 * instantiation problem.
 */
public class CompilerInstantiationException extends CompilerException
{
	private static final long serialVersionUID = -1432532276931215491L;

	public enum ProblemType 
	{
		NO_COMPILERS_FOUND,
		MULTIPLE_COMPILERS_FOUND,
		ERROR_CREATING_COMPILER
	}
	
	private ProblemType type;
	private Collection<String> compilerPlugins;
	private String compilerPluginName = "unknow";
	
	public CompilerInstantiationException(ProblemType type)
	{
		super();
		this.type = type;
	}

	public CompilerInstantiationException(Collection<String> compilerPlugins)
	{
		this(ProblemType.MULTIPLE_COMPILERS_FOUND);
		this.compilerPlugins = compilerPlugins; 
	}

	public CompilerInstantiationException(CoreException e,
				String compilerPluginName)
	{
		super(e);
		type = ProblemType.ERROR_CREATING_COMPILER;
		this.compilerPluginName = compilerPluginName;
	}

	public ProblemType getProblemType()
	{
		return type;
	}
	

	public Collection<String> getCompilerPlugins()
	{
		return compilerPlugins;
	}

	public String getMessage()
	{
		String message = "";
		
		switch (type)
		{
		case NO_COMPILERS_FOUND:
			message = "No plugins define extension for " + 
				CorePlugin.COMPILER_EXTENSION_ID;
			break;
		case MULTIPLE_COMPILERS_FOUND:
			message = "Multiple plugins define extension for " + 
				CorePlugin.COMPILER_EXTENSION_ID;
			break;
		case ERROR_CREATING_COMPILER:
			message = "Error while instantiating IModelicaCompiler class " + 
				" provided by '" + compilerPluginName + "' plugin. " +
				getCause().getMessage();
			break;
		}
		
		return message;
	}

	public String getCompilerPlugin()
	{
		return compilerPluginName;
	}
}
