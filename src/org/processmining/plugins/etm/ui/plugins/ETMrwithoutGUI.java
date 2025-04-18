package org.processmining.plugins.etm.ui.plugins;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.etm.CentralRegistry;
import org.processmining.plugins.etm.ETM;
import org.processmining.plugins.etm.experiments.StandardLogs;
import org.processmining.plugins.etm.logging.EvolutionLogger;
import org.processmining.plugins.etm.model.narytree.NAryTree;
import org.processmining.plugins.etm.model.narytree.conversion.NAryTreeToProcessTree;
import org.processmining.plugins.etm.model.narytree.conversion.ProcessTreeToNAryTree;
import org.processmining.plugins.etm.parameters.ETMParam;
import org.processmining.plugins.etm.parameters.ETMParamFactory;
import org.processmining.processtree.ProcessTree;
import org.uncommonseditedbyjoosbuijs.watchmaker.framework.EvolutionObserver;

@Plugin(
		name = "Mine a Process Tree with ETMr using parameters and classifier",
			parameterLabels = { "Process Tree", "Event log", "Classifier", "Parameters", "Log File Path Relative"},
			returnLabels = { "Process Tree" },
			returnTypes = { ProcessTree.class },
			userAccessible = true,
			help = "Repair a Process Tree with ETMr using parameters and classifier",
			handlesCancel = true,
			categories = {PluginCategory.Enhancement},
			keywords = {"ETM", "ETMr", "Process Tree", "Evolutionary", "Evolutionary Tree Miner", "Genetic", "Genetic Miner"})
public class ETMrwithoutGUI {

	@PluginVariant(
			variantLabel = "Mine a Process Tree with ETMr using default parameters and provided classifier",
				requiredParameterLabels = { 0, 1, 2, 3 })
	public static ProcessTree minePTWithClassifier(final PluginContext context, ProcessTree processTree, XLog eventlog,  XEventClassifier classifier, String logFilePathRelative) {
		ETMParam param = ETMParamFactory.buildStandardParam(eventlog, context);
		
		return minePTWithParameters(context, processTree, eventlog, classifier, param, logFilePathRelative);
	}

	@PluginVariant(
			variantLabel = "Mine a Process Tree with ETMr using provided parameters and provided classifier",
				requiredParameterLabels = { 0, 1, 2, 3, 4 })
	public static ProcessTree minePTWithParameters(final PluginContext context, ProcessTree processTree, XLog eventlog, XEventClassifier classifier, ETMParam param, String logFilePathRelative) {

		//TODO check if this is enough, should be..
		param.getCentralRegistry().updateEventClassifier(classifier);
		
		NAryTree[] seed = new NAryTree[1];
		ProcessTreeToNAryTree convertor = new ProcessTreeToNAryTree(param.getCentralRegistry().getEventClasses());
		seed[0] = convertor.convert(processTree);
		
		param.setSeed(seed);		
//		String logFilePathRelative = "./Results/"
		
		//Add a logger to output to the context
		List<EvolutionObserver<NAryTree>> evolutionObservers = new ArrayList<EvolutionObserver<NAryTree>>();
		evolutionObservers.add(new EvolutionLogger<NAryTree>(context, param.getCentralRegistry(), true, 1, logFilePathRelative));
		param.setEvolutionObservers(evolutionObservers);		
		
		ETM etm = new ETM(param);
		etm.run();

		NAryTree nat = etm.getResult();

		ProcessTree pt = NAryTreeToProcessTree.convert(nat, param.getCentralRegistry().getEventClasses());

		return pt;
	}

}
