//
//  DataHandler.h
//  LiveAndGov Urban Planning
//
//  Created by George Liaros on 2/21/14.
//
//

#import <Foundation/Foundation.h>
#import <ASIHTTPRequest/ASIHTTPRequest.h>
#import <ASIHTTPRequest/ASIFormDataRequest.h>
#import "DataHandlerDelegate.h"
#import "AREntityData.h"
#import "AppDelegate.h"

#define SERVICE_CENTRE_URL @"https://urbanplanning.yucat.com/servicecenter/api"
#define URBAN_PLANNING_API_URL @"https://urbanplanning.yucat.com/dialogandvisualization/api"

#define SERVICE_CENTER_USERNAME @"up_client_account"
#define SERVICE_CENTER_PASSWORD @"tsyucat"

#define defaults [NSUserDefaults standardUserDefaults]


@protocol DataHandlerDelegate;

@interface DataHandler : NSObject
{
//    static NSDictionary *basque = @{@"18 - 35" : @"18 - 35"
//                                            , @"36 - 65" : @"36 - 65"
//                                            , @"About" : @"Zeri buruz"
//                                            , @"Age" : @"Adina"
//                                            , @"AR Layer" : @"EA Kapa"
//                                            , @"Area of residence" : @"Auzoa"
//                                            , @"Basque" : @"Euskara"
//                                            , @"Billboard" : @"Etiketak"
//                                            , @"Cannot connect to server.\nPlease check your internet connection" : @"Ez da possible zerbitzariarekin konektatzea"
//                                            , @"Done" : @"Eginda"
//                                            , @"Downloading ..." : @"Deskargatzen..."
//                                            , @"Empty Fields" : @"Hutsuneak betetzeko"
//                                            , @"English" : @"Ingelesa"
//                                            , @"Error" : @"Akatza"
//                                            , @"Error loading questionnaire" : @"Akatsa galdeketa kargatzerakoan"
//                                            , @"Female" : @"Emakumea"
//                                            , @"Gender" : @"Genero"
//                                            , @"Irazagorria" : @"Irazagorria"
//                                            , @"Language" : @"Hizkuntza"
//                                            , @"List" : @"Lista"
//                                            , @"Male" : @"Gizona"
//                                            , @"Map" : @"Mapa"
//                                            , @"Navigation" : @"Nabigazioa"
//                                            //                   , @"Nearby" : @"Ingurukoa"
//                                            , @"Nearby" : @"Hurbiltasunaren arabera"
//                                            , @"No" : @"Ez"
//                                            , @"None" : @"Bat ere"
//                                            , @"No internet connection" : @"Internetekin konexio falta dago"
//                                            , @"OK" : @"Zuzena"
//                                            , @"Over the 65" : @"65 urtetik gorakoak"
//                                            , @"Personal Information" : @"Datu pertsonalak"
//                                            , @"Please fill all the questions" : @"Mesedez erantzun itzazu galdera guztiak"
//                                            , @"Please provide your information" : @"Mesedez bete itzazu zure datuak."
//                                            , @"Ponton Urarte" : @"Ponton Urarte"
//                                            , @"Provide Opinion" : @"Iritzia eman"
//                                            , @"Provide opinion" : @"Iritzia eman"
//                                            , @"Questionnaire is not valid" : @"Kuestionarioa ez da zuzena"
//                                            , @"Questionnaire updated." : @"Kuestionarioa eguneratuta"
//                                            , @"Refresh" : @"Freskatu"
//                                            , @"Resident in Gordexola" : @"Gordexolan auzokoa"
//                                            , @"Results" : @"Emaitzak"
//                                            , @"Save" : @"Gorde"
//                                            //                   , @"Scan" : @"Irudi bidez"
//                                            , @"Scan" : @"Irudiaren arabera"
//                                            , @"Select an answer" : @"Erantzun bat aukeratu"
//                                            , @"Something went wrong while submitting the questionnaire" : @"Kuestionario hau bidaltzerakoan akatza agertu da"
//                                            , @"Spanish" : @"Gaztelania"
//                                            , @"Submit" : @"Bidali"
//                                            , @"Success" : @"Zuzena"
//                                            , @"Under the 18" : @"18 urtetik beherakoak"
//                                            , @"User information stored successfully" : @"Erabiltzailearen datuak era egokian gorde dira."
//                                            , @"User name:" : @"Erabiltzailearen izena"
//                                            , @"Write here.." : @"Idatzi hemen..."
//                                            , @"Yes" : @"Bai"
//                                            , @"Your name" : @"Zure izena:"
//                                            , @"Your Opinion" : @"Zure iritzia"
//                                            , @"Zaldu" : @"Zaldu"
//                                            , @"Zandamendi" : @"Zandamendi"
//                                            , @"Zubiete" : @"Zubiete"
//                                            
//                                            };

}
- (void) createAnonymousUser;
- (NSString*) createAnonymousUserSynchronous;
- (BOOL) getUserQuestionnaire: (BOOL) update;
- (void) getPermissions;
- (void) getAREntities;
- (NSMutableDictionary*) getQuestionaireWithOptions: (NSArray*) options;
- (void) getModel:(AREntity*) entity Number:(NSInteger) modelNo;
- (NSString*) getIBSTrackingPath;
- (void) setUserInformation:(NSString*) name
                        Age:(NSString*) age
                        Sex:(NSString*) sex
              ResidenceZone:(NSString*) residenceZone
                   Language:(NSString*) language;

- (void) setUserInformation:(NSString *)name
                        Age:(NSString *)age
                        Sex:(NSString *)sex
              ResidenceZone:(NSString *)residenceZone;

- (void) storeUserv1:(NSInteger)value1
                  v2:(NSInteger)value2
                  v4:(NSInteger)value4
                  v5:(NSInteger)value5;
- (BOOL) validateQuestionnaire:(NSDictionary*) questionnaire;
- (BOOL)submitQuestionnaire:(NSDictionary *)questionnaire WithLat: (CLLocationDegrees) latitude AndLon: (CLLocationDegrees) longitude;
+ (NSString*) getLocalizedString:(NSString*) key;
- (NSString*) getIBS;
+ (NSDictionary*)getBasque;
@property(nonatomic, weak) id<DataHandlerDelegate> delegate;
@end
