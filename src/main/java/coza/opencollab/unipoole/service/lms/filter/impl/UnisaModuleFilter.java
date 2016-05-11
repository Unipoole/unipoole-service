package coza.opencollab.unipoole.service.lms.filter.impl;

import coza.opencollab.unipoole.service.lms.filter.ModuleFilter;
import coza.opencollab.unipoole.shared.Module;
import java.text.MessageFormat;
import java.util.regex.Pattern;

/**
 * A module filter that understand the naming standards of
 * UNISA.
 * <p>
 * The course format works like this:
 * AAC411A-06-Y2
 * 1. The first is the module code (AAC411A), 7 characters
 * 2. The second '06' is the year, here 2006
 * 3. The third is the semester 'Y2'.  We have four periods: (the 0 for Y1 is what is stored in the student database, don’t think you are going to need that but just in case).
 * Y1 = 0, Year course
 * Y2 = 6, Year course starting in the second part of the year.
 * S1 = 1, Semester 1
 * S2 = 2, Semester 2
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class UnisaModuleFilter extends AllowAllModuleFilter implements ModuleFilter{
    /**
     * The pattern for the complete module name.
     */
    private static final String modulePattern = "{1}[a-zA-Z_0-9]{0}-{2}-{3}";
    /**
     * The module year.
     */
    private String year;
    /**
     * The module semester.
     */
    private String semester;
    /**
     * The module code, or part thereof.
     */
    private String moduleCode;
    /**
     * The complete module name pattern. This is calculated every time one of the values are set.
     */
    private Pattern pattern;
    /**
     * Whether to include the master site only or all 
     * sites for the module.
     */
    private boolean masterOnly = false;

    /**
     * Default constructor.
     */
    public UnisaModuleFilter(){}
    
    /**
     * Set all constructor.
     */
    public UnisaModuleFilter(String year, String semester, String moduleCode){
        this(year, semester, moduleCode, false);
    }
    
    /**
     * Set all constructor.
     */
    public UnisaModuleFilter(String year, String semester, String moduleCode, boolean masterOnly){
        this.year = year;
        this.semester = semester;
        this.moduleCode = moduleCode;
        this.masterOnly = masterOnly;
        calcModuleName();
    }

    /**
     * The module year.
     */
    public void setYear(String year) {
        this.year = year;
        calcModuleName();
    }

    /**
     * The module semester.
     */
    public void setSemester(String semester) {
        this.semester = semester;
        calcModuleName();
    }

    /**
     * The module code, or part thereof.
     */
    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
        calcModuleName();
    }
    
    /**
     * Build the module name from moduleCode, year and semester.
     */
    private void calcModuleName(){
        String p = MessageFormat.format(modulePattern, "{" + (7 - moduleCode.length()) + "}", moduleCode, year, semester);
        if(!masterOnly){
            p = p + "-[a-zA-Z_0-9]+";
        }else{
            p = MessageFormat.format(modulePattern, "{" + (7 - moduleCode.length()) + "}", moduleCode, year, "Master");
        }
        pattern = Pattern.compile(p);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allow(Module module) {
        return pattern.matcher(module.getName()).matches();
    }
}
