//
//  ResultsTableViewController.m
//  Urban Planning
//
//  Created by George Liaros on 3/6/14.
//  Copyright (c) 2014 George Liaros. All rights reserved.
//

#import "ResultsTableViewController.h"

@interface ResultsTableViewController ()
@property (nonatomic, strong) CPTGraphHostingView *hostView;
@property (nonatomic, strong) CPTTheme *selectedTheme;

-(void)initPlot;
-(void)configureHost;
-(void)configureGraph;
-(void)configureChart;
-(void)configureLegend;
@end

@implementation ResultsTableViewController
@synthesize hostView;// = hostView_;
@synthesize selectedTheme;// = selectedTheme_;
@synthesize theQuestionnaire;
- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    self.title = [[[[self.theQuestionnaire objectForKey:@"questionnaireresult"] objectForKey:@"categoriesresult"] objectAtIndex:0] objectForKey:@"name"];
    questions = [[[[self.theQuestionnaire objectForKey:@"questionnaireresult"] objectForKey:@"categoriesresult"] objectAtIndex:0] objectForKey:@"questionsresult"];
//    NSLog(@"questions desc = %@", [questions description]);
    headerViews = [[NSMutableArray alloc] init];
    footerViews = [[NSMutableArray alloc] init];
    for(int i = 0; i < [questions count];i++)
    {
        if([[[questions objectAtIndex:i] objectForKey:@"displaytype"] integerValue]== 2)
            continue;
        UILabel *headerView = [[UILabel alloc] init];
        headerView.text = [[questions objectAtIndex:i] objectForKey:@"questiontext"];
        [headerViews addObject:headerView];
        CGRect parentRect = CGRectMake(0.0, 0.0, 320.0, 250.0);
        CPTGraphHostingView *footerView = [(CPTGraphHostingView *) [CPTGraphHostingView alloc] initWithFrame:parentRect];
        [self configureGraphForHostingView:footerView andTitle:[[questions objectAtIndex:i] objectForKey:@"questiontext"] andIdentifier:[questions objectAtIndex:i]];
        [footerViews addObject:footerView];
    }
    
    id tracker = [[GAI sharedInstance] defaultTracker];
    
    // This screen name value will remain set on the tracker and sent with
    // hits until it is set to a new value or to nil.
    [tracker set:kGAIScreenName
           value:@"Results View"];
    
    // manual screen tracking
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
    // The plot is initialized here, since the view bounds have not transformed for landscape till now
//    [self initPlot];
}
- (void)viewDidLoad
{
    [super viewDidLoad];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
#warning Potentially incomplete method implementation.
    // Return the number of sections.
    return [footerViews count];
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    return [footerViews objectAtIndex:section];
}


-(CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
//    return 250.0;
    return 330.0f;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
//    return [headerViews objectAtIndex:section];
    return nil;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
//    return 200.0;
    return 0.0;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
#warning Incomplete method implementation.
    // Return the number of rows in the section.
    return 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    
    // Configure the cell...
    
    return cell;
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }   
    else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
#pragma mark - Table view delegate

// In a xib-based application, navigation from a table can be handled in -tableView:didSelectRowAtIndexPath:
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here, for example:
    // Create the next view controller.
    <#DetailViewController#> *detailViewController = [[<#DetailViewController#> alloc] initWithNibName:@"<#Nib name#>" bundle:nil];

    // Pass the selected object to the new view controller.
    
    // Push the view controller.
    [self.navigationController pushViewController:detailViewController animated:YES];
}
 
 */


-(void)initPlot {
    [self configureHost];
    [self configureGraph];
    [self configureChart];
    [self configureLegend];
}

-(void)configureHost {
	// 1 - Set up view frame
//	CGRect parentRect = self.view.bounds;
    CGRect parentRect = CGRectMake(0.0, 0.0, 320.0, 250.0);
    //	CGSize toolbarSize = self.toolbar.bounds.size;
    //    CGSize toolbarSize = self.navigationController.toolbar.bounds.size;
    //	parentRect = CGRectMake(parentRect.origin.x,
    //							(parentRect.origin.y + toolbarSize.height),
    //							parentRect.size.width,
    //							(parentRect.size.height - toolbarSize.height));
	// 2 - Create host view
	self.hostView = [(CPTGraphHostingView *) [CPTGraphHostingView alloc] initWithFrame:parentRect];
    //	self.hostView.allowPinchScaling = NO;
//	[self.view addSubview:self.hostView];
}

- (void) configureGraphForHostingView:(CPTGraphHostingView*) hostingView andTitle:(NSString*) title andIdentifier:(NSDictionary*) identifier
{
//    NSLog(@"identifier desc = %@", identifier);
    CPTGraph *graph = [[CPTXYGraph alloc] initWithFrame:self.hostView.bounds];
	hostingView.hostedGraph = graph;
//    graph.identifier = identifier;
    
	graph.paddingLeft = 0.0f;
	graph.paddingTop = 0.0f;
	graph.paddingRight = 0.0f;
	graph.paddingBottom = 0.0f;
	graph.axisSet = nil;
	// 2 - Set up text style
	CPTMutableTextStyle *textStyle = [CPTMutableTextStyle textStyle];
	textStyle.color = [CPTColor grayColor];
	textStyle.fontName = @"Helvetica";
	textStyle.fontSize = 13.0f;
	// 3 - Configure title
//	NSString *title = @"Portfolio Prices: May 1, 2012";
    NSString *titlest = [identifier objectForKey:@"questiontext"];
    if([titlest length] > 150)
    {
        NSArray *splitted =  [titlest componentsSeparatedByCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
        NSMutableString *mut = [[NSMutableString alloc] init];
        for(int i = 0 ; i < [splitted count]; i++)
        {
            [mut appendString:[splitted objectAtIndex:i]];
            if(i==[splitted count]/5 || i==2*[splitted count]/5 || i == 3*[splitted count]/5 || i== 4*[splitted count]/5)
            {
                [mut appendString:@"\n"];
            }
            else
            {
                [mut appendString:@" "];
            }
            
        }
        titlest = [mut copy];
//        titlest = [NSString stringWithFormat:@"%@\n%@", [titlest substringToIndex:[titlest length]/2], [titlest substringFromIndex:[titlest length]/2+1]];
    }
    else if([titlest length] > 100)
    {
        NSArray *splitted = [titlest componentsSeparatedByCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
        NSMutableString *mut = [[NSMutableString alloc] init];
        for( int i = 0 ; i < [splitted count]; i++)
        {
            [mut appendString:[splitted objectAtIndex:i]];
            if(i==[splitted count]/4 || i==2*[splitted count]/4 || i==3*[splitted count]/4){
                [mut appendString:@"\n"];
            }
            else{
                [mut appendString:@" "];
            }
        }
        titlest = [mut copy];
    }
    else if( [titlest length] > 50)
    {
        NSArray *splitted = [titlest componentsSeparatedByCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
        NSMutableString *mut = [[NSMutableString alloc] init];
        for(int i = 0; i < [splitted count]; i++)
        {
            [mut appendString:[splitted objectAtIndex:i]];
            if(i== [splitted count]/3 || i == 2*[splitted count]/3){
                [mut appendString:@"\n"];
            }
            else{
                [mut appendString:@" "];
            }
        }
        titlest = [mut copy];
    }
	graph.title = titlest;
	graph.titleTextStyle = textStyle;
	graph.titlePlotAreaFrameAnchor = CPTRectAnchorTop;
	graph.titleDisplacement = CGPointMake(2.0f, -12.0f);
	// 4 - Set theme
	[graph applyTheme:[CPTTheme themeNamed:kCPTPlainWhiteTheme]];
    CPTPieChart *pieChart = [[CPTPieChart alloc] init];
	pieChart.dataSource = self;
	pieChart.delegate = self;
	pieChart.pieRadius = (hostingView.bounds.size.height * 0.5) / 2;
//	pieChart.identifier = graph.title;
    pieChart.identifier = identifier;
	pieChart.startAngle = M_PI_4;
	pieChart.sliceDirection = CPTPieDirectionClockwise;
	// 3 - Create gradient
	CPTGradient *overlayGradient = [[CPTGradient alloc] init];
	overlayGradient.gradientType = CPTGradientTypeRadial;
	overlayGradient = [overlayGradient addColorStop:[[CPTColor blackColor] colorWithAlphaComponent:0.0] atPosition:0.9];
	overlayGradient = [overlayGradient addColorStop:[[CPTColor blackColor] colorWithAlphaComponent:0.4] atPosition:1.0];
	pieChart.overlayFill = [CPTFill fillWithGradient:overlayGradient];
	// 4 - Add chart to graph
	[graph addPlot:pieChart];
	// 2 - Create legend
	CPTLegend *theLegend = [CPTLegend legendWithGraph:graph];
	// 3 - Configure legen
	theLegend.numberOfColumns = 1;
	theLegend.fill = [CPTFill fillWithColor:[CPTColor whiteColor]];
	theLegend.borderLineStyle = [CPTLineStyle lineStyle];
	theLegend.cornerRadius = 5.0;
    CPTMutableTextStyle *style = [CPTTextStyle textStyle];
    style.fontSize = 10.0f;
    theLegend.textStyle = style;
	// 4 - Add legend to graph
	graph.legend = theLegend;
//	graph.legendAnchor = CPTRectAnchorRight;
    graph.legendAnchor =  CPTRectAnchorBottomRight;
	CGFloat legendPadding = -(self.view.bounds.size.width / 8);
	graph.legendDisplacement = CGPointMake(-10.0, 0.0);

}
-(void)configureGraph {
	// 1 - Create and initialise graph
	CPTGraph *graph = [[CPTXYGraph alloc] initWithFrame:self.hostView.bounds];
	self.hostView.hostedGraph = graph;
	graph.paddingLeft = 0.0f;
	graph.paddingTop = 0.0f;
	graph.paddingRight = 0.0f;
	graph.paddingBottom = 0.0f;
	graph.axisSet = nil;
	// 2 - Set up text style
	CPTMutableTextStyle *textStyle = [CPTMutableTextStyle textStyle];
	textStyle.color = [CPTColor grayColor];
	textStyle.fontName = @"Helvetica-Bold";
	textStyle.fontSize = 16.0f;
	// 3 - Configure title
	NSString *title = @"Portfolio Prices: May 1, 2012";
	graph.title = title;
	graph.titleTextStyle = textStyle;
	graph.titlePlotAreaFrameAnchor = CPTRectAnchorTop;
	graph.titleDisplacement = CGPointMake(0.0f, -12.0f);
	// 4 - Set theme
	self.selectedTheme = [CPTTheme themeNamed:kCPTPlainWhiteTheme];
	[graph applyTheme:self.selectedTheme];
}

-(void)configureChart {
}

-(void)configureLegend {
	// 1 - Get graph instance
//	CPTGraph *graph = self.hostView.hostedGraph;
//	// 2 - Create legend
//	CPTLegend *theLegend = [CPTLegend legendWithGraph:graph];
//	// 3 - Configure legen
//	theLegend.numberOfColumns = 1;
//	theLegend.fill = [CPTFill fillWithColor:[CPTColor whiteColor]];
//	theLegend.borderLineStyle = [CPTLineStyle lineStyle];
//	theLegend.cornerRadius = 5.0;
//	// 4 - Add legend to graph
//	graph.legend = theLegend;
//	graph.legendAnchor = CPTRectAnchorRight;
//	CGFloat legendPadding = -(self.view.bounds.size.width / 8);
//	graph.legendDisplacement = CGPointMake(legendPadding, 0.0);
}

-(NSUInteger)numberOfRecordsForPlot:(CPTPlot *)plot {
//    NSLog(@"dataLabelForPlot");
//    NSLog(@"count desc1 = %@", [plot.identifier description]);
    NSDictionary *question = (NSDictionary*) plot.identifier;
//    NSLog(@"desc = %@", [question description]);
//    NSLog(@"count desc = %@", [question description]);
//    NSLog(@"count = %d", [[question objectForKey:@"answeroptionsresult"] count]);
	return [[question objectForKey:@"answeroptionsresult"] count];
}

-(NSNumber *)numberForPlot:(CPTPlot *)plot field:(NSUInteger)fieldEnum recordIndex:(NSUInteger)index {
//	if (CPTPieChartFieldSliceWidth == fieldEnum) {
//		return [NSNumber numberWithDouble:index * 3.5];
//	}
    NSDictionary *question = (NSDictionary*) plot.identifier;
//    NSLog(@"number for plot = %d", [[[[question objectForKey:@"answeroptionsresult"] objectAtIndex:index] objectForKey:@"answeredcount"] integerValue]);
	return [[[question objectForKey:@"answeroptionsresult"] objectAtIndex:index] objectForKey:@"answeredcount"];
//    NSLog(@"dataLabelForPlot");
}

-(CPTLayer *)dataLabelForPlot:(CPTPlot *)plot recordIndex:(NSUInteger)index {
	// 1 - Define label text style
	static CPTMutableTextStyle *labelText = nil;
	if (!labelText) {
		labelText= [[CPTMutableTextStyle alloc] init];
		labelText.color = [CPTColor grayColor];
	}
    NSDictionary *question = (NSDictionary*) plot.identifier;
//    NSString *text = [[[question objectForKey:@"answeroptionsresult"] objectAtIndex:index] objectForKey:@"answeroptiontext"];
    NSNumber *votesNo = [[[question objectForKey:@"answeroptionsresult"] objectAtIndex:index] objectForKey:@"answeredcount"];
    if([votesNo integerValue] == 0)
        return nil;
    else
        return [[CPTTextLayer alloc] initWithText:[votesNo stringValue] style:labelText];
	// 2 - Calculate portfolio total value
//	NSDecimalNumber *portfolioSum = [NSDecimalNumber zero];
//    //	for (NSDecimalNumber *price in [[CPDStockPriceStore sharedInstance] dailyPortfolioPrices]) {
//    //		portfolioSum = [portfolioSum decimalNumberByAdding:price];
//    //	}
//	// 3 - Calculate percentage value
//	NSNumber *price = [NSNumber numberWithDouble:6.5 *index];
//    //	NSDecimalNumber *percent = [price decimalNumberByDividingBy:portfolioSum];
//	// 4 - Set up display label
//	NSString *labelValue = [NSString stringWithFormat:@"$%0.2f USD (%0.1f %%)", [price floatValue], ([price floatValue] * 100.0f)];
//	// 5 - Create and return layer with label text
//    NSLog(@"dataLabelForPlot");
}

-(NSString *)legendTitleForPieChart:(CPTPieChart *)pieChart recordIndex:(NSUInteger)index {
    //	if (index < [[[CPDStockPriceStore sharedInstance] tickerSymbols] count]) {
    //		return [[[CPDStockPriceStore sharedInstance] tickerSymbols] objectAtIndex:index];
    //	}
//    NSLog(@"dataLabelForPlot");
    NSDictionary *question = (NSDictionary*) pieChart.identifier;
    NSString *answer = [[[question objectForKey:@"answeroptionsresult"] objectAtIndex:index] objectForKey:@"answeroptiontext"];
    NSNumber *votesNo = [[[question objectForKey:@"answeroptionsresult"] objectAtIndex:index] objectForKey:@"answeredcount"];
    if([votesNo integerValue] == 0)
        return nil;
    else
        return answer;
//    return [NSString stringWithFormat:@"%@ : %d Votes", answer, [votesNo integerValue]];
//	return [[[question objectForKey:@"answeroptionsresult"] objectAtIndex:index] objectForKey:@"answeroptiontext"];
}

@end
