//
// Class : UIProp
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.3 $
// $Date: 2001/10/10 17:39:19 $
// $Author: ed $
//

package org.concord.biologica.ui;

import java.io.Serializable;

import java.lang.IllegalArgumentException;
import java.lang.String;

/**
 * This class contains definitions for BioLogica UI properties.<p>
 *
 * These property names are used to notify listeners of property
 * changes in the BioLogica user interface (ui) classes.<p>
 *
 * The property names are here because we wanted to standardize on
 * a given set of names across the ui, rather than having each
 * engine class define its own names.<p>
 *
 * @see	java.beans.PropertyChangeEvent
 * @see	java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.3 $ $Date: 2001/10/10 17:39:19 $
 * @author 		$Author: ed $
**/
public final class UIProp
{
    // Please keep these in alphabetical order

    /**
     * The active tool in a view has changed.
     * New and old values are Integers
    **/
    public static final String ACTIVE_TOOL = "activeTool";

    /**
     * Alignment controls visible in big meiosis view.
     * New and old values are Boolean.
    **/
    public static final String ALIGNMENT_CONTROLS_VISIBLE = "alignmentControlsVisible";

    /**
     * Apply button pushed.  Note that this event occurs whether or
     * not the apply is defered.
     * New and old values are null.
    **/
    public static final String APPLY_BUTTON_PUSHED = "applyButtonPushed";

    /**
     * The background color changed.
     * New and old values are Color's.
    **/
    public static final String BACKGROUND = "background";

    /**
     * Indicates that the magnify button in the big father meiosis view was pushed.
     * New and old values are null.
    **/
    public static final String BIG_FATHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED = "bigFatherMeiosisMagnifyButtonPushed";

    /**
     * Indicates that the magnify button in the big fertilization view was pushed.
     * New and old values are null.
    **/
    public static final String BIG_FERTILIZATION_MAGNIFY_BUTTON_PUSHED = "bigFertilizationMagnifyButtonPushed";

    /**
     * Indicates that the magnify button in the big mother meiosis view was pushed.
     * New and old values are null.
    **/
    public static final String BIG_MOTHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED = "bigMotherMeiosisMagnifyButtonPushed";
    
    public static final String MEIOSIS_CHROMOSOME_SELECTED = "meiosisChromosomeSelected";
    /**
     * Children have been added to a family in the view.
     * Old value is null and should be ignored.
     * New value is the existing Family object.
    **/
    public static final String CHILDREN_ADDED_TO_VIEW = "childrenAddedToView";

    /**
     * Chromosomes to show in the chromosome view.
     * New and old values are Integers.
    **/
    public static final String CHROMOSOMES_TO_SHOW = "chromosomesToShow";

    /**
     * The enabled state of the chromosome tool has changed.
     * New and old values are Boolean's indicating the enabled state of the chromosome tool.
    **/
    public static final String CHROMOSOME_TOOL_ENABLED = "chromosomeToolEnabled";

    /**
     * The visibility of the chromosome tool has changed.
     * New and old values are Boolean's indicating the visibility of the chromosome tool.
    **/
    public static final String CHROMOSOME_TOOL_VISIBLE = "chromosomeToolVisible";

    /**
     * Chromosome tool was used to pick on an organism.  The listener must do whatever
     * is appropriate as the view doesn't have the ability to know what should be done.
     * New value is the organism picked on.  Old value is null.
    **/
    public static final String CHROMOSOME_TOOL_PICK_ON_ORGANISM = "chromosomeToolPickOnOrganism";

    /**
     * A cross failed in the pedigree view, usually because the user
     * tried to cross to organisms of the same sex.  The old value is
     * the first potential parent organism and the new value is the
     * second potential parent organism or null if the mouse up was
     * not over an organism.<p>
    **/
    public static final String CROSS_FAILED = "crossFailed";

    /**
     * A cross was done successfully in the pedigree view.  The old value is
     * null and the new value is the created Family.
    **/
    public static final String CROSS_SUCCEEDED = "crossSucceeded";

    /**
     * The enabled state of the cross tool has changed.
     * New and old values are Boolean's indicating the enabled state of the cross tool.
    **/
    public static final String CROSS_TOOL_ENABLED	= "crossToolEnabled";

    /**
     * The visibility of the cross tool has changed.
     * New and old values are Boolean's indicating the visibility of the cross tool.
    **/
    public static final String CROSS_TOOL_VISIBLE	= "crossToolVisible";

    /**
     * Crossover controls visible in big meiosis view.
     * New and old values are Boolean.
    **/
    public static final String CROSSOVER_CONTROLS_VISIBLE = "crossoverControlsVisible";

    /**
     * The flag indicating whether a view should draw the text for an organism's
     * characteristics under the organism has changed.
     * New and old values are Boolean objects.
    **/
    public static final String CHARACTERISTICS_TEXT_VISIBLE = "characteristicsTextVisible";

    /**
     * Defer Apply flag changed.
     * New and old values are Boolean.
    **/
    public static final String DEFER_APPLY = "deferApply";

    /**
     * Defer Revert flag changed.
     * New and old values are Boolean.
    **/
    public static final String DEFER_REVERT = "deferRevert";

    /**
     * A Family object has been added to a view.
     * Old value is null and should be ignored.
     * New value is the added Family object.
    **/
    public static final String FAMILY_ADDED_TO_VIEW = "familyAddedToView";

    /**
     * A Family object has been removed from a view.
     * Old value is null and should be ignored.
     * New value is the removed Family object.
    **/
    public static final String FAMILY_REMOVED_FROM_VIEW = "familyRemovedFromView";

    /**
     * The father organism shown in a view has changed from one organism to another.
     * This event is fired by a view which involves mating (e.g. sex view).
     * New and old values are Organism objects and either may be null.<p>
    **/
    public static final String FATHER_ORGANISM	= "fatherOrganism";

    /**
     * Fertilization manual disabled state changed.  Used by scripts to turn
     * off fertilization until some script-determined state is reached.<p>
     * New and old values are Booleans
    **/
    public static final String FERTILIZATION_MANUALLY_DISABLED	= "fertilizationManuallyDisabled";

    /**
     * The fertilization gametes have changed.
     * New and old values are ignored.
    **/
    public static final String FERTILIZATION_GAMETES	= "fertilizationGametes";

    /**
     * The fertilization model has changed.  Used in the small and big fertilization
     * views to indicate that a new model has been assigned to the view.
     * New and old values are FertilizationModel's.
    **/
    public static final String FERTILIZATION_MODEL = "fertilizationModel";

    /**
     * The fertilization offspring organism has changed, usually either
     * becoming null or becoming non-null.
     * New and old values are Organism.
    **/
    public static final String FERTILIZATION_OFFSPRING_ORGANISM	= "fertilizationOffspringOrganism";

    /**
     * Fertilization has started (scripts shouldn't use this - it's internal)
     * New and old values are integers (the new and old steps).
    **/
    public static final String FERTILIZATION_STARTED = "fertilizationStarted";

    /**
     * Fertilization has started and was stopped.
     * New and old values are integers (the new and old steps).
    **/
    public static final String FERTILIZATION_STARTED_AND_STOPPED = "fertilizationStartedAndStopped";

    /**
     * The fertilization step has changed.
     * New and old values are Integers.
    **/
    public static final String FERTILIZATION_STEP = "fertilizationStep";

    /**
     * The fixed number of children from a breeding has changed.  This
     * number is used in the pedigree view and other views.<p>
     * New and old values are Integer objects.
    **/
    public static final String FIXED_NUMBER_CHILDREN = "fixedNumberChildren";

    /**
     * The font changed.
     * New and old values are Font's.
    **/
    public static final String FONT = "font";

    /**
     * The foreground color changed.
     * New and old values are Color's.
    **/
    public static final String FOREGROUND = "foreground";
    
    /**
     * The flag indicating whether a view should draw the lock symbol
     * when an organism is locked has changed.
     * New and old values are Boolean objects.
    **/
    public static final String LOCK_SYMBOL_VISIBLE = "lockSymbolVisible";

    /**
     * The maximum number of children from a breeding has changed.  This
     * number is used in the pedigree view and other views.<p>
     * New and old values are Integer objects.
    **/
    public static final String MAXIMUM_NUMBER_CHILDREN = "maximumNumberChildren";

    /**
     * The meiosis model has changed.  Used in the small and big meiosis views
     * to indicate that a new model has been assigned to the view.
     * New and old values are MeiosisModel's.
    **/
    public static final String MEIOSIS_MODEL = "meiosisModel";

    /**
     * The father meiosis has started and was stopped.
     * New and old values are Integers (the new and old steps).
    **/
    public static final String MEIOSIS_FATHER_STARTED_AND_STOPPED = "meiosisFatherStartedAndStopped";

    /**
     * The father meiosis step of the sex view has changed.
     * New and old values are Integers.
    **/
    public static final String MEIOSIS_FATHER_STEP = "meiosisFatherStep";

    /**
     * Meiosis of the mother has started and was stopped.
     * New and old values are integers (the new and old steps).
    **/
    public static final String MEIOSIS_MOTHER_STARTED_AND_STOPPED = "meiosisMotherStartedAndStopped";

    /**
     * The mother meiosis step of the sex view has changed.
     * New and old values are Integers.
    **/
    public static final String MEIOSIS_MOTHER_STEP = "meiosisMotherStep";

    /**
     * Meiosis has started.  This is an internal event and scripts shouldn't use it.
     * Instead they should use MEIOSIS_MOTHER_STARTED_AND_STOPPED and
     * MEIOSIS_FATHER_STARTED_AND_STOPPED.<p>
     * New and old values are integers (the new and old steps).
    **/
    public static final String MEIOSIS_STARTED = "meiosisStarted";

    /**
     * The meiosis step of a subview (mother or father) has changed.  This is
     * an internal event and scripts shouldn't use it.  Instead they should use
     * MEIOSIS_FATHER_STEP and MEIOSIS_MOTHER_STEP.<p>
     * New and old values are Integers.
    **/
    public static final String MEIOSIS_STEP = "meiosisStep";

    /**
     * The minimum number of children from a breeding has changed.  This
     * number is used in the pedigree view and other views.
     * New and old values are Integer objects.<p>
    **/
    public static final String MINIMUM_NUMBER_CHILDREN = "minimumNumberChildren";

    /**
     * The minimum and/or maximum number of children from a breeding has changed.
     * This number is used in the pedigree view and other views.
     * New and old values are null, because there are 2 values (min & max) that
     * may have changed.  So you'll need to go ask the view for those values.<p>
    **/
    public static final String MINMAX_NUMBER_CHILDREN = "minMaxNumberChildren";

    /**
     * The mother organism shown in a view has changed from one organism to another.
     * This event is fired by a view which involves mating (e.g. sex view).
     * New and old values are Organism objects and either may be null.<p>
    **/
    public static final String MOTHER_ORGANISM	= "motherOrganism";

    /**
     * The view's movable state has changed.
     * New and old values are Boolean
    **/
    public static final String MOVABLE = "movable";

    /**
     * The moved father gamete in a sex view has changed.
     * New and old values are Integer's indicating the moved gamete.
    **/
    public static final String MOVED_FATHER_GAMETE = "movedFatherGamete";

    /**
     * The moved gamete in a meiosis model has changed.
     * New and old values are Integer's indicating the moved gamete.
    **/
    public static final String MOVED_GAMETE = "movedGamete";

    /**
     * The moved mother gamete in a sex view has changed.
     * New and old values are Integer's indicating the moved gamete.
    **/
    public static final String MOVED_MOTHER_GAMETE = "movedMotherGamete";

    /**
     * The flag indicating whether a view should draw the text for an organism's
     * name under the organism has changed.
     * New and old values are Boolean objects.
    **/
    public static final String NAME_TEXT_VISIBLE = "nameTextVisible";

    /**
     * The number of female children to create in a family when the
     * pedigree view is in OFFSPRING_MODE_MALE_FEMALE mode.
     * New and old values are Integers.
    **/
    public static final String NUMBER_FEMALE_CHILDREN = "numberFemaleChildren";

    /**
     * The number of male children to create in a family when the
     * pedigree view is in OFFSPRING_MODE_MALE_FEMALE mode.
     * New and old values are Integers.
    **/
    public static final String NUMBER_MALE_CHILDREN = "numberMaleChildren";

    /**
     * The object shown in a view has changed from one object to another
     * This event is typically fired by a view which contains a single
     * engine object of any type (e.g. the object properties view).
     * New and old values are EngineObject objects and either may be null.<p>
    **/
    public static final String OBJECT = "object";

    /**
     * The pedigree view offspring mode has changed.
     * Old and new values are Integers.
    **/
    public static final String OFFSPRING_MODE = "offspringMode";

    /**
     * The enabled state of the offspring mode pulldown has changed.
     * New and old values are Boolean's indicating the enabled state of the offspring mode pulldown.
    **/
    public static final String OFFSPRING_MODE_PULLDOWN_ENABLED	= "offspringModePulldownEnabled";

    /**
     * The visibility of the offspring mode pulldown has changed.
     * New and old values are Boolean's indicating the visibility of the offspring mode pulldown.
    **/
    public static final String OFFSPRING_MODE_PULLDOWN_VISIBLE	= "offspringModePulldownVisible";

    /**
     * The offspring organism shown in a view has changed from one organism to another.
     * This event is fired by a view which involves mating (e.g. sex view).
     * New and old values are Organism objects and either may be null.<p>
    **/
    public static final String OFFSPRING_ORGANISM	= "offspringOrganism";

    /**
     * The organism shown in a view has changed from one organism to another.
     * This event is typically fired by a view which contains a single
     * organism (e.g. the chromosome view, DNA view, single organism view, etc.).
     * New and old values are Organism objects and either may be null.<p>
    **/
    public static final String ORGANISM	= "organism";

    /**
     * An Organism object has been added to a view.
     * New value is the added Organism object.
     * Old value is null and should be ignored.
    **/
    public static final String ORGANISM_ADDED_TO_VIEW = "organismAddedToView";

    /**
     * The OrganismAllele object has been changed in a view, most likely
     * in the DNA View.
     * New and old values are OrganismAllele objects.
    **/
    public static final String ORGANISM_ALLELE = "organismAllele";

    /**
     * The OrganismAllelePair object has been changed in a view, most likely
     * in the DNA View.
     * New and old values are OrganismAllelePair objects.
    **/
    public static final String ORGANISM_ALLELE_PAIR = "organismAllelePair";

    /**
     * The organism image size for a view has changed.
     * New and old values are Integers for the old and new sizes.
    **/
    public static final String ORGANISM_IMAGE_SIZE = "organismImageSize";

    /**
     * The organism images visibility in the pedigree view has changed.
     * New and old values are Booleans.
    **/
    public static final String ORGANISM_IMAGES_VISIBLE = "organismImagesVisible";

    /**
     * Organism layout style, used in MultipleOrganismView.
     * New and old values are Integers for the old and new styles.
    **/
    public static final String ORGANISM_LAYOUT_STYLE = "organismLayoutStyle";

    /**
     * An Organism object has been removed from a view and deselected.
     * New value is the removed Organism object.
     * Old value is null and should be ignored.
    **/
    public static final String ORGANISM_REMOVED_FROM_VIEW = "organismRemovedFromView";

    /**
     * The percentage of a sex view devoted to organism views.
     * New and old values are Integer's between 0 and 100.
    **/
    public static final String ORGANISM_VIEW_HEIGHT_PERCENTAGE = "organismViewHeightPercentage";

    /**
     * Pedigree tool was used to pick on an organism.  The listener must do whatever
     * is appropriate as the view doesn't have the ability to know what should be done.
     * New value is the organism picked on.  Old value is null.
    **/
    public static final String PEDIGREE_TOOL_PICK_ON_ORGANISM = "pedigreeToolPickOnOrganism";

    /**
     * The visibility of the pedigree tool has enabled.
     * New and old values are Boolean's indicating the enabled state of the pedigree tool.
    **/
    public static final String PEDIGREE_TOOL_ENABLED	= "pedigreeToolEnabled";

    /**
     * The visibility of the pedigree tool has changed.
     * New and old values are Boolean's indicating the visibility of the pedigree tool.
    **/
    public static final String PEDIGREE_TOOL_VISIBLE	= "pedigreeToolVisible";

    /**
     * Replay button enabled in sex view.
     * New and old values are Boolean.
    **/
    public static final String REPLAY_BUTTON_ENABLED = "replayButtonEnabled";

    /**
     * Replay button visible in sex view.
     * New and old values are Boolean.
    **/
    public static final String REPLAY_BUTTON_VISIBLE = "replayButtonVisible";

    /**
     * Is a reset automatic when an offspring is created in sex view.
     * New and old values are Boolean.
    **/
    public static final String RESET_AUTOMATIC = "resetAutomatic";

    /**
     * Reset button enabled in sex view.
     * New and old values are Boolean.
    **/
    public static final String RESET_BUTTON_ENABLED = "resetButtonEnabled";

    /**
     * Reset button visible in sex view.
     * New and old values are Boolean.
    **/
    public static final String RESET_BUTTON_VISIBLE = "resetButtonVisible";

    /**
     * Reset should delete offspring organism in sex view.
     * New and old values are Boolean.
    **/
    public static final String RESET_DELETE_OFFSPRING_ORGANISM = "resetDeleteOffspringOrganism";

    /**
     * Revert button pushed.  Note that this event occurs whether or
     * not the revert is defered.
     * New and old values are null.
    **/
    public static final String REVERT_BUTTON_PUSHED = "revertButtonPushed";

    /**
     * The root engine object in the tree view changed.
     * New value is the new EngineObject used for the root.
     * Old value is the old EngineObject used for the root.
    **/
    public static final String ROOT_ENGINE_OBJECT = "rootEngineObject";

    /**
     * The scale of a view has changed.
     * New and old values are Integers
    **/
    public static final String SCALE = "scale";

    /**
     * The selected state of the event source has changed.
     * New and old values are Boolean.<p>
    **/
    public static final String SELECTED = "selected";

    /**
     * The selected allele of the shown organism in a chromosome view has changed.
     * New and old values are OrganismAllele objects and either may be null.<p>
    **/
    public static final String SELECTED_ALLELE	= "selectedAllele";

    /**
     * The selected bases of a string of DNA has changed.
     * New and old values are null.  Receivers of this notification
     * must query the source object for the currently selected bases.<p>
    **/
    public static final String SELECTED_BASES = "selectedBases";

    /**
     * The selected chromosome of the shown organism in a chromosome view has changed.
     * New and old values are OrganismChromosome objects and either may be null.<p>
    **/
    public static final String SELECTED_CHROMOSOME = "selectedChromosome";

    /**
     * The selected families in a view have changed.
     * New value is the deselected Family object.
     * Old value is null and should be ignored.
    **/
    public static final String SELECTED_FAMILIES = "selectedFamilies";

    /**
     * The selected father gamete in a sex view has changed.
     * New and old values are Integer's indicating the selected gamete.
    **/
    public static final String SELECTED_FATHER_GAMETE = "selectedFatherGamete";

    /**
     * The selected gamete in a meiosis model has changed.
     * New and old values are Integer's indicating the selected gamete.
    **/
    public static final String SELECTED_GAMETE = "selectedGamete";

    /**
     * The selected mother gamete in a sex view has changed.
     * New and old values are Integer's indicating the selected gamete.
    **/
    public static final String SELECTED_MOTHER_GAMETE = "selectedMotherGamete";

    /**
     * The selected objects in a selection set has changed.
     * Old and new values are null and should be ignored.
    **/
    public static final String SELECTED_OBJECTS = "selectedObjects";

    /**
     * The selection mode of a view has changed.
     * New and old values are Integer's indicating the view selection mode.
    **/
    public static final String SELECTION_MODE		= "selectionMode";

    /**
     * The visibility of the selection tool has enabled.
     * New and old values are Boolean's indicating the enabled state of the selection tool.
    **/
    public static final String SELECTION_TOOL_ENABLED	= "selectionToolEnabled";

    /**
     * Selection tool was used to pick on a static organism.  This is a special case
     * event that notifies a user that the user has clicked on an organism in the
     * static organism view.  It is not a true selection and the receive of this event
     * must decide what the mouse click means.
     * New value is the organism picked on.  Old value is null.
    **/
    public static final String SELECTION_TOOL_PICK_ON_STATIC_ORGANISM = "selectionToolPickOnStaticOrganism";

    /**
     * The visibility of the selection tool has changed.
     * New and old values are Boolean's indicating the visibility of the selection tool.
    **/
    public static final String SELECTION_TOOL_VISIBLE	= "selectionToolVisible";

    /**
     * The flag indicating whether a view should draw the text for an organism's
     * sex under the organism has changed.
     * New and old values are Boolean objects.
    **/
    public static final String SEX_TEXT_VISIBLE = "sexTextVisible";
    
    /**
     * The sex view mode changed (e.g. 6 views, big meiosis, big fertilization, etc.).
     * New and old values are Integer's indicating the view mode.
    **/
    public static final String SEX_VIEW_MODE		= "sexViewMode";

    /**
     * Indicates that the magnify button in the small father meiosis view was pushed.
     * New and old values are null.
    **/
    public static final String SMALL_FATHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED = "smallFatherMeiosisMagnifyButtonPushed";

    /**
     * Indicates that the magnify button in the small fertilization view was pushed.
     * New and old values are null.
    **/
    public static final String SMALL_FERTILIZATION_MAGNIFY_BUTTON_PUSHED = "smallFertilizationMagnifyButtonPushed";

    /**
     * Indicates that the magnify button in the small mother meiosis view was pushed.
     * New and old values are null.
    **/
    public static final String SMALL_MOTHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED = "smallMotherMeiosisMagnifyButtonPushed";

    /**
     * The enabled state of the snip tool has changed.
     * New and old values are Boolean's indicating the enabled state of the snip tool.
    **/
    public static final String SNIP_TOOL_ENABLED = "snipToolEnabled";

    /**
     * The visibility of the snip tool has changed.
     * New and old values are Boolean's indicating the visibility of the snip tool.
    **/
    public static final String SNIP_TOOL_VISIBLE = "snipToolVisible";

    /**
     * Snip tool was used to pick on a family.
     * The listener must do whatever is appropriate as the view doesn't have
     * the ability to know what should be done.
     * New value is the family picked on.  Old value is null.
    **/
    public static final String SNIP_TOOL_PICK_ON_FAMILY = "snipToolPickOnFamily";

    /**
     * Snip tool was used to pick on an organism.
     * The listener must do whatever is appropriate as the view doesn't have
     * the ability to know what should be done.
     * New value is the organism picked on.  Old value is null.
    **/
    public static final String SNIP_TOOL_PICK_ON_ORGANISM = "snipToolPickOnOrganism";
    
    /**
     * The flag indicating whether a view should draw the text for an organism's
     * species under the organism has changed.
     * New and old values are Boolean objects.
    **/
    public static final String SPECIES_TEXT_VISIBLE = "speciesTextVisible";

    /**
     * Stop immediately when fertilization starts.
     * New and old values are Boolean.
    **/
    public static final String STOP_WHEN_FERTILIZATION_STARTS = "stopWhenFertilizationStarts";

    /**
     * Stop immediately when meiosis starts.
     * New and old values are Boolean.
    **/
    public static final String STOP_WHEN_MEIOSIS_STARTS = "stopWhenMeiosisStarts";

    /**
     * The text indent of the text underneath organisms in the multiple organism view.
     * New and old values are Integer objects.
    **/
    public static final String TEXT_INDENT = "textIndent";

    /**
     * The text line spacing of the text underneath organisms in the multiple organism view.
     * New and old values are Integer objects.
    **/
    public static final String TEXT_LINE_SPACING = "textLineSpacing";

    /**
     * Indicates the trait of the source view changed.
     * New and old values are of type Trait and may be null.
    **/
    public static final String TRAIT = "trait";

    /**
     * The enabled state of the trait pulldown has changed.
     * New and old values are Boolean's indicating the enabled state of the trait pulldown.
    **/
    public static final String TRAIT_PULLDOWN_ENABLED	= "traitPulldownEnabled";

    /**
     * The visibility of the trait pulldown has changed.
     * New and old values are Boolean's indicating the visibility of the trait pulldown.
    **/
    public static final String TRAIT_PULLDOWN_VISIBLE	= "traitPulldownVisible";

    /**
     * A window has closed.
     * New value is the window.  Old value is null.<p>
    **/
    public static final String WINDOW_CLOSED		= "windowClosed";
}

